package com.curtin.mathtest.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.curtin.mathtest.Model.Question;
import com.curtin.mathtest.R;

import org.w3c.dom.Text;

import java.util.Objects;


public class QuestionFragment extends Fragment {
    private Question question;
    public QuestionFragment(Question q) {
        this.question = q;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        TextView questionText = (TextView) view.findViewById(R.id.questionText);
        TextView timerText = (TextView) view.findViewById(R.id.timerText);
        return view;
    }




}