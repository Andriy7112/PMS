package ua.kpi.comsys.io8102.ui.student;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.Date;
import java.util.ArrayList;
import java.util.HashMap;

import ua.kpi.comsys.io8102.R;

public class StudentFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StudentViewModel studentViewModel = new ViewModelProvider(this).get(StudentViewModel.class);
        View root = inflater.inflate(R.layout.fragment_student, container, false);
        final TextView textView = root.findViewById(R.id.text_name);
        studentViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });


        // First part

        Contents contents = new Contents();
        System.out.println("Завдання 1");
        HashMap<String, ArrayList<String>> groupedStudents =
                contents.groupStudents(Contents.studentStr);
        System.out.println();


        System.out.println("Завдання 2");
        int[] points = new int[] {12, 12, 12, 12, 12, 12, 12, 16};
        HashMap<String, HashMap<String, ArrayList<Integer>>> grades =
                contents.fillGrades(groupedStudents, points);
        System.out.println();


        System.out.println("Завдання 3");
        HashMap<String, HashMap<String, Integer>> gradesSum = contents.showGradesSum(grades);
        System.out.println();


        System.out.println("Завдання 4");
        HashMap<String, Float> middle = contents.showAvgGradesInGroups(gradesSum);
        System.out.println();


        System.out.println("Завдання 5");
        HashMap<String, ArrayList<String>> top = contents.showBestInGroups(gradesSum);
        System.out.println();


        // Second part
        TimeAV timeFirst = new TimeAV();
        TimeAV timeSecond = new TimeAV(11,30,59);
        TimeAV timeThird = new TimeAV(new Date());
        System.out.println(timeSecond.toString());
        System.out.println(timeFirst.toString());
        System.out.println(timeThird.toString());
        System.out.println(timeSecond.addTime(timeThird));
        System.out.println(TimeAV.addTwo(timeSecond, timeThird));
        System.out.println(timeSecond.subtractTime(timeThird));
        System.out.println(TimeAV.subtractTwo(timeSecond, timeThird));
        System.out.println(TimeAV.addTwo(new TimeAV(12,0,1),
                new TimeAV(23, 59, 59)));
        System.out.println(TimeAV.subtractTwo(new TimeAV(0,0,0),
                new TimeAV(0, 0, 1)));

        return root;
    }
}