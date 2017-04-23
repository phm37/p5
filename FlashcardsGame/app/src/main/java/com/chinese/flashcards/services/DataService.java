package com.chinese.flashcards.services;

import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.chinese.flashcards.R;
import com.chinese.flashcards.models.Card;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataService extends Service {

    private final String downloadTitle        = "Chinese Dictionary Download";
    private final String downloadDescription  = "Downloading dictionary of Chinese characters and their corresponding pinyin and english meaning.";
    private              DownloadManager downloadManager;
    private              boolean         isDataReady;
    private              List<Card>      cachedCards;

    private final IBinder serviceBinder = new ServiceBinder<DataService>(DataService.this);

    public long download(String url) {
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        // Enqueue download request
        request.setTitle(this.downloadTitle);
        request.setDescription(this.downloadDescription);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        final long downloadID = this.downloadManager.enqueue(request);

        try {
            OnDownloadCompleted(downloadID);
        } catch (Exception ex){}

        return downloadID;
    }

    private boolean OnDownloadCompleted(long downloadID) throws FileNotFoundException {
        Boolean downloadStatus = null;

        while (downloadStatus == null) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterByStatus(DownloadManager.STATUS_FAILED|DownloadManager.STATUS_SUCCESSFUL);
            Cursor c = downloadManager.query(query);

            if(c != null && c.moveToFirst()) {
                int statusIndex     = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int downloadIDIndex = c.getColumnIndex(DownloadManager.COLUMN_ID);

                if((c.getInt(statusIndex) == DownloadManager.STATUS_SUCCESSFUL) && (c.getLong(downloadIDIndex) == downloadID)){
                    String filename       = getApplicationContext().getResources().getString(R.string.DictionaryFileName);
                    ParcelFileDescriptor downloadedFile = this.downloadManager.openDownloadedFile(downloadID);
                    File dictionaryFile   = new File(getApplicationContext().getFilesDir() + "/" + filename);

                    try {
                        copy(downloadedFile, dictionaryFile);
                        downloadedFile.close();
                        isDataReady = downloadStatus = true;
                    } catch (Exception e) {
                        isDataReady = downloadStatus = false;
                        e.printStackTrace();
                    }
                } else if ((c.getInt(statusIndex) == DownloadManager.STATUS_FAILED) && (c.getLong(downloadIDIndex) == downloadID)){
                    isDataReady = downloadStatus = false;
                    break;
                }
            }
        }

        return downloadStatus;
    }

    public List<Card> read(File file) throws Exception {
        if (!file.exists() || !file.canRead() || !file.isFile()) {
            return null;
        }
        FileReader fileReader  = new FileReader(file);
        Scanner    fileScanner = new Scanner(fileReader);
        List<Card> cards       = new ArrayList<Card>();

        while (fileScanner.hasNextLine()) {
            String entry   = fileScanner.nextLine();
            String[] parts = entry.split(",");

            Card card = new Card(getApplicationContext(), parts[0].trim(), parts[1].trim(), parts[2].trim());
            cards.add(card);
        }

        return cards;
    }

    public void copy(ParcelFileDescriptor sourceFile, File distFile) throws Exception {
        if (!distFile.exists()) {
            distFile.createNewFile();
        }
        FileInputStream sourceFileReader  = new FileInputStream(sourceFile.getFileDescriptor());
        Scanner         sourceFileScanner = new Scanner(sourceFileReader);
        PrintWriter     destPrintWriter   = new PrintWriter(new FileWriter(distFile, false));

        while (sourceFileScanner.hasNext()) {
            String nextLine = sourceFileScanner.nextLine();

            // Stop immediately if invalid input is found
            if (!nextLine.matches("^(.+),(.+),(.+)$")) {
                sourceFileScanner.close();
                sourceFileReader.close();
                destPrintWriter.close();
                throw new Exception("Unrecognized file content.");
            }

            destPrintWriter.println(nextLine);
        }

        sourceFileScanner.close();
        sourceFileReader.close();
        destPrintWriter.close();
    }

    public List<Card> getCards(boolean forceUpdate) throws Exception {

        // Get Dictionary URL from SharedPreferences. If it is not
        // specified there (should never happen), fall back to string resource.
        String filename     = getApplicationContext().getResources().getString(R.string.DictionaryFileName);
        String fileUrl      = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                               .getString(getApplicationContext().getResources().getString(R.string.DictionaryUrlPreference),
                                                          getApplicationContext().getResources().getString(R.string.DefaultDictionaryUrl));
        File dictionaryFile = new File(getApplicationContext().getFilesDir() + "/" + filename);

        // If data is not ready, request downloading the file and
        // block until the file is downloaded and copied to internal storage
        if (!this.isDataReady) {
            this.download(fileUrl);
        }

        if (this.isDataReady && forceUpdate) {
            List<Card> cards = this.read(dictionaryFile);

            if (cards != null && !cards.isEmpty()) {
                this.cachedCards = cards;
            }
        }

        return this.cachedCards;
    }

    /**
     * Attempts to re-download the dictionary file and save it in
     * internal storage.
     */
    public boolean update() {
        this.isDataReady = false;
        try {
            this.getCards(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this.isDataReady;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        this.downloadManager = (DownloadManager) getApplicationContext().getSystemService(Context.DOWNLOAD_SERVICE);
        String filename      = getApplicationContext().getResources().getString(R.string.DictionaryFileName);
        File dictionaryFile  = new File(filename);
        this.isDataReady     = dictionaryFile.exists();
        this.cachedCards     = null;

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return this.serviceBinder;
    }
}
