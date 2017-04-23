package com.chinese.flashcards.activities;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chinese.flashcards.R;
import com.chinese.flashcards.models.Question;
import com.chinese.flashcards.services.QuizService;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link QuestionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link QuestionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class QuestionFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private QuizService quizService;
    private Question    question;

    private OnFragmentInteractionListener mListener;

    public QuestionFragment() {
        // Required empty public constructor
    }

    public static QuestionFragment newInstance(Intent intent) {
        QuestionFragment fragment = new QuestionFragment();
        fragment.setArguments(intent.getExtras());
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_question, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get Question and QuizService from parent activity
        this.quizService = ((QuizActivity)getActivity()).getServiceConnection().getService();
        this.question    = ((QuizActivity)getActivity()).getCurrentQuestion();

        // Add listeners to the RadioGroups to control when next Question will show up
        RadioGroup first_choice_radio_group  = (RadioGroup)getActivity().findViewById(R.id.first_choice_radio_group);
        RadioGroup second_choice_radio_group = (RadioGroup)getActivity().findViewById(R.id.second_choice_radio_group);

        first_choice_radio_group.setOnCheckedChangeListener(this);
        second_choice_radio_group.setOnCheckedChangeListener(this);
    }
    @Override
    public void onStart() {
        super.onStart();

        // Get Language Modes from resources
        String englishMode = this.getResources().getString(R.string.EnglishMode);
        String chineseMode = this.getResources().getString(R.string.ChineseMode);
        String pinyinMode  = this.getResources().getString(R.string.PinyinMode);

        // Get View components
        TextView questionView                = (TextView)getActivity().findViewById(R.id.question_text);
        TextView   first_choice_mode         = (TextView)getActivity().findViewById(R.id.first_choice_mode);
        TextView   second_choice_mode        = (TextView)getActivity().findViewById(R.id.second_choice_mode);
        RadioGroup first_choice_radio_group  = (RadioGroup)getActivity().findViewById(R.id.first_choice_radio_group);
        RadioGroup second_choice_radio_group = (RadioGroup)getActivity().findViewById(R.id.second_choice_radio_group);

        int         rightChoiceIndex = 0;
        RadioButton radioButton      = null;

        if (this.question.language.equalsIgnoreCase(englishMode)) {
            questionView.setText(question.card.english);

            //
            // Chinese
            //
            first_choice_mode.setText(chineseMode);

            // Correct Choice
            rightChoiceIndex = new Random().nextInt(3);
            radioButton = (RadioButton)first_choice_radio_group.getChildAt(rightChoiceIndex);
            radioButton.setText(this.question.card.chinese);

            // Bad choices
            for (int i = 0; i < first_choice_radio_group.getChildCount(); i++) {
                if (i == rightChoiceIndex)
                    continue;

                radioButton = (RadioButton)first_choice_radio_group.getChildAt(i);
                radioButton.setText(this.quizService.getCard(question.choices.get(i)).chinese);
            }

            //
            // Pinyin
            //
            second_choice_mode.setText(pinyinMode);

            // Correct Choice
            rightChoiceIndex = new Random().nextInt(3);
            radioButton = (RadioButton)second_choice_radio_group.getChildAt(rightChoiceIndex);
            radioButton.setText(this.question.card.pinyin);

            // Bad choices
            for (int i = 0; i < second_choice_radio_group.getChildCount(); i++) {
                if (i == rightChoiceIndex)
                    continue;

                radioButton = (RadioButton)second_choice_radio_group.getChildAt(i);
                radioButton.setText(this.quizService.getCard(question.choices.get(i)).pinyin);
            }
        }

        else if (this.question.language.equalsIgnoreCase(chineseMode)) {
            questionView.setText(question.card.chinese);

            //
            // English
            //
            first_choice_mode.setText(englishMode);

            // Correct Choice
            rightChoiceIndex = new Random().nextInt(3);
            radioButton = (RadioButton)first_choice_radio_group.getChildAt(rightChoiceIndex);
            radioButton.setText(this.question.card.english);

            // Bad choices
            for (int i = 0; i < first_choice_radio_group.getChildCount(); i++) {
                if (i == rightChoiceIndex)
                    continue;

                radioButton = (RadioButton)first_choice_radio_group.getChildAt(i);
                radioButton.setText(this.quizService.getCard(question.choices.get(i)).english);
            }

            //
            // Pinyin
            //
            second_choice_mode.setText(pinyinMode);

            // Correct Choice
            rightChoiceIndex = new Random().nextInt(3);
            radioButton = (RadioButton)second_choice_radio_group.getChildAt(rightChoiceIndex);
            radioButton.setText(this.question.card.pinyin);

            // Bad choices
            for (int i = 0; i < second_choice_radio_group.getChildCount(); i++) {
                if (i == rightChoiceIndex)
                    continue;

                radioButton = (RadioButton)second_choice_radio_group.getChildAt(i);
                radioButton.setText(this.quizService.getCard(question.choices.get(i)).pinyin);
            }
        }

        else if (this.question.language.equalsIgnoreCase(pinyinMode)) {
            questionView.setText(question.card.pinyin);

            //
            // Chinese
            //
            first_choice_mode.setText(chineseMode);

            // Correct Choice
            rightChoiceIndex = new Random().nextInt(3);
            radioButton = (RadioButton)first_choice_radio_group.getChildAt(rightChoiceIndex);
            radioButton.setText(this.question.card.chinese);

            // Bad choices
            for (int i = 0; i < first_choice_radio_group.getChildCount(); i++) {
                if (i == rightChoiceIndex)
                    continue;

                radioButton = (RadioButton)first_choice_radio_group.getChildAt(i);
                radioButton.setText(this.quizService.getCard(question.choices.get(i)).chinese);
            }

            //
            // English
            //
            second_choice_mode.setText(englishMode);

            // Correct Choice
            rightChoiceIndex = new Random().nextInt(3);
            radioButton = (RadioButton)second_choice_radio_group.getChildAt(rightChoiceIndex);
            radioButton.setText(this.question.card.english);

            // Bad choices
            for (int i = 0; i < second_choice_radio_group.getChildCount(); i++) {
                if (i == rightChoiceIndex)
                    continue;

                radioButton = (RadioButton)second_choice_radio_group.getChildAt(i);
                radioButton.setText(this.quizService.getCard(question.choices.get(i)).english);
            }
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        // Make sure both RadioGroups are selected
        RadioGroup first_choice_radio_group  = (RadioGroup)getActivity().findViewById(R.id.first_choice_radio_group);
        RadioGroup second_choice_radio_group = (RadioGroup)getActivity().findViewById(R.id.second_choice_radio_group);

        int firstGroupResult  = first_choice_radio_group.getCheckedRadioButtonId();
        int secondGroupResult = second_choice_radio_group.getCheckedRadioButtonId();

        if (firstGroupResult < 0 || secondGroupResult < 0)
            return;

        // Get Mode of RadioGroups
        TextView   first_choice_mode  = (TextView)getActivity().findViewById(R.id.first_choice_mode);
        TextView   second_choice_mode = (TextView)getActivity().findViewById(R.id.second_choice_mode);

        // Get choices on both RadioGroups
        String firstGroupChoice  = ((RadioButton)getActivity().findViewById(firstGroupResult)).getText().toString();
        String secondGroupChoice = ((RadioButton)getActivity().findViewById(secondGroupResult)).getText().toString();


        // Store results on Question
        this.question.response.put(first_choice_mode.getText().toString(), firstGroupChoice);
        this.question.response.put(second_choice_mode.getText().toString(), secondGroupChoice);

        // Set selected choices and enable next_question_button button
        FloatingActionButton nextQuestionButton = (FloatingActionButton)getActivity().findViewById(R.id.next_question_button);
        if (nextQuestionButton.getVisibility() == View.INVISIBLE)
            nextQuestionButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
