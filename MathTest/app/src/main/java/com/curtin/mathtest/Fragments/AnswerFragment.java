package com.curtin.mathtest.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.curtin.mathtest.Model.Answer;
import com.curtin.mathtest.R;

import java.util.List;


public class AnswerFragment extends Fragment implements FragmentUpdater<Answer> {


    private Button answer1;
    private Button answer2;
    private Button answer3;
    private Button answer4;

    private Button nextButton;
    private Button previousButton;

    private Button nextQuestionButton;
    private Button endTestButton;

    private View view;
    private Answer answer;
    private List<Answer> answers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_answer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        answer1 = (Button) view.findViewById(R.id.firstAnswer);
        answer2 = (Button) view.findViewById(R.id.secondAnswer);
        answer3 = (Button) view.findViewById(R.id.thirdAnswer);
        answer4 = (Button) view.findViewById(R.id.fourthAnswer);

        nextButton = (Button) view.findViewById(R.id.moreAnswers);
        previousButton = (Button) view.findViewById(R.id.previousAnswers);
        nextQuestionButton = (Button) view.findViewById(R.id.nextQuestionButton);
        endTestButton = (Button) view.findViewById(R.id.endTestButton);
    }

    @Override
    public void updateFragment(Answer answer) {

    }

    @Override
    public void insertData(Answer answer) {

    }
}