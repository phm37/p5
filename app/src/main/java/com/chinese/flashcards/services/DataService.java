package com.chinese.flashcards.services;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;

import com.chinese.flashcards.R;
import com.chinese.flashcards.models.ApplicationContext;
import com.chinese.flashcards.models.Card;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataService extends ApplicationContext {

    private static final String          downloadTitle        = "Chinese Dictionary Download";
    private static final String          downloadDescription  = "Downloading dictionary of Chinese characters and their corresponding pinyin and english meaning.";
    private static final  boolean        downloadVisibleInUi  = false;
    private        final DownloadManager downloadManager;
    private              boolean         isDataReady;

    public DataService(Context c) {
        super(c);
        this.downloadManager = (DownloadManager) this.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        String filename      = this.getContext().getResources().getString(R.string.DictionaryFileName);
        File dictionaryFile  = new File(filename);
        this.isDataReady     = dictionaryFile.exists();
    }

    public long download(String url) {
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        // Enqueue download request
        request.setTitle(this.downloadTitle);
        request.setDescription(this.downloadDescription);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        //request.setVisibleInDownloadsUi(this.downloadVisibleInUi);
        //request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Test");
        final long downloadID = this.downloadManager.enqueue(request);

        OnDownloadCompleted(downloadID);
        // Setup receiver to wait for download completion
//        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//        BroadcastReceiver receiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent)  {
//                long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
//                if (downloadID == reference) {
//                    Uri downloadedFileUri = downloadManager.getUriForDownloadedFile(downloadID);
//                    String filename       = getContext().getResources().getString(R.string.DictionaryFileName);
//                    File dictionaryFile   = new File(filename);
//                    File downloadedFile   = new File(downloadedFileUri.getPath());
//                    try {
//                        copy(downloadedFile, dictionaryFile);
//                        downloadedFile.delete();
//                        isDataReady = true;
//                    } catch (IOException e) {
//                        isDataReady = false;
//                        e.printStackTrace();
//                    }
//                }
//            }
//        };

        //this.getContext().registerReceiver(receiver, filter);
        return downloadID;
    }

    //
    // @TODO Fix Download - cannot read downloaded file
    //
    private boolean OnDownloadCompleted(long downloadID) {
        Boolean downloadStatus = null;

        while (downloadStatus == null) {
            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterByStatus(DownloadManager.STATUS_FAILED|DownloadManager.STATUS_SUCCESSFUL);
            Cursor c = downloadManager.query(query);

            if(c != null && c.moveToFirst()) {
                int statusIndex     = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int downloadIDIndex = c.getColumnIndex(DownloadManager.COLUMN_ID);
                String uriString = c.getString(c.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                Uri downloadedFileUri = Uri.parse(uriString);
                if((c.getInt(statusIndex) == DownloadManager.STATUS_SUCCESSFUL) && (c.getLong(downloadIDIndex) == downloadID)){
                    //Uri downloadedFileUri = downloadManager.getUriForDownloadedFile(downloadID);
                    String filename       = getContext().getResources().getString(R.string.DictionaryFileName);
                    String downloadedFilePath = "/" + downloadedFileUri.getAuthority() + downloadedFileUri.getPath();
                    File dictionaryFile   = new File(this.getContext().getFilesDir() + "/" + filename);
                    File downloadedFile   = new File(downloadedFilePath);
                    try {
                        copy(downloadedFile, dictionaryFile);
                        downloadedFile.delete();
                        isDataReady = downloadStatus = true;
                    } catch (IOException e) {
                        isDataReady = downloadStatus = false;
                        e.printStackTrace();
                    }
                }
            }
        }

        return downloadStatus;
    }

    public List<Card> read(File file) throws FileNotFoundException {
        if (!file.exists() || !file.canRead() || !file.isFile()) {
            return null;
        }
        FileReader fileReader  = new FileReader(file);
        Scanner    fileScanner = new Scanner(fileReader);
        List<Card> cards       = new ArrayList<Card>();

        while (fileScanner.hasNextLine()) {
            String entry   = fileScanner.nextLine();
            String[] parts = entry.split(",");

            //if (parts.length != 15) {
            //    continue;
           // }

            Card card = new Card(this.getContext(), parts[0], parts[1], parts[2]);
            cards.add(card);
        }

        return cards;
    }

    public void save(File destFile) throws IOException {
        destFile.deleteOnExit();
        destFile.createNewFile();

    }

    public void copy(File sourceFile, File distFile) throws IOException {
       // if (!sourceFile.exists() || !sourceFile.canRead() || !sourceFile.isFile()) {
       //     return;
        // }

        if (!distFile.exists()) {
            distFile.createNewFile();
        }

        FileReader  sourceFileReader  = new FileReader(sourceFile);
        Scanner     sourceFileScanner = new Scanner(sourceFileReader);
        PrintWriter destPrintWriter   = new PrintWriter(new FileWriter(distFile, false));

        while (sourceFileScanner.hasNext()) {
            destPrintWriter.println(sourceFileScanner.nextLine());
        }

        sourceFileScanner.close();
        sourceFileReader.close();
        destPrintWriter.close();
    }

    public void update() {

    }

    public List<Card> getCards() throws IOException {

        // Check whether file exists in internal storage
        String filename     = this.getContext().getResources().getString(R.string.DictionaryFileName);
        String fileUrl      = this.getContext().getResources().getString(R.string.DefaultDictionaryUrl);
        File dictionaryFile = new File(this.getContext().getFilesDir() + "/" + filename);

        // If data is not ready, request downloading the file and
        // block until the file is downloaded and copied to storage service
        if (!this.isDataReady) {
            this.download(fileUrl);
            while (!this.isDataReady) {}
        }

        return this.read(dictionaryFile);
    }
}
