package ua.kpi.comsys.io8102.ui.student;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Contents {
    public static final String studentStr = "Дмитренко Олександр - ІП-84; Матвійчук Андрій - ІВ-83; " +
            "Лесик Сергій - ІО-82; Ткаченко Ярослав - ІВ-83; Аверкова Анастасія - ІО-83; " +
            "Соловйов Даніїл - ІО-83; Рахуба Вероніка - ІО-81; Кочерук Давид - ІВ-83; " +
            "Лихацька Юлія- ІВ-82; Головенець Руслан - ІВ-83; Ющенко Андрій - ІО-82; " +
            "Мінченко Володимир - ІП-83; Мартинюк Назар - ІО-82; Базова Лідія - ІВ-81; " +
            "Снігурець Олег - ІВ-81; Роман Олександр - ІО-82; Дудка Максим - ІО-81; " +
            "Кулініч Віталій - ІВ-81; Жуков Михайло - ІП-83; Грабко Михайло - ІВ-81; " +
            "Іванов Володимир - ІО-81; Востриков Нікіта - ІО-82; Бондаренко Максим - ІВ-83; " +
            "Скрипченко Володимир - ІВ-82; Кобук Назар - ІО-81; Дровнін Павло - ІВ-83; " +
            "Тарасенко Юлія - ІО-82; Дрозд Світлана - ІВ-81; Фещенко Кирил - ІО-82; " +
            "Крамар Віктор - ІО-83; Іванов Дмитро - ІВ-82";



    // TASK 1

    public HashMap<String, ArrayList<String>> groupStudents(String studentStr) {
        HashMap<String, ArrayList<String>> studentsGroups = new HashMap<>();
        String[] splitted1 = studentStr.split("; ");
        ArrayList<String> splitted2 = new ArrayList<>();
        String elem;

        for (String student : splitted1) {
            String[] studGroupPair = student.split(" - ");
            if (studGroupPair.length == 2) {
                splitted2.add(studGroupPair[0]);
                splitted2.add(studGroupPair[1]);
            }
        }

        for (int i = 0; i < splitted2.size(); i++) {
            elem = splitted2.get(i);
            if (i % 2 == 1) {
                if (!studentsGroups.containsKey(elem))
                    studentsGroups.put(elem, new ArrayList<>());

                studentsGroups.get(elem).add(splitted2.get(i - 1));
            }
        }

        for (Map.Entry<String, ArrayList<String>> group: studentsGroups.entrySet()) {
            Collections.sort(group.getValue());
        }

        studentsGroups.entrySet()
                .stream()
                .sorted(Map.Entry.<String, ArrayList<String>>comparingByKey())
                .forEach(System.out::println);

        return studentsGroups;

    }

    // TASK 2

    private int randomValue(int maxValue) {
        Random random = new Random();
        switch (random.nextInt(6)) {
            case 1:
                return (int) Math.ceil((float) maxValue * 0.7);
            case 2:
                return (int) Math.ceil((float) maxValue * 0.9);
            case 3:
            case 4:
            case 5:
                return maxValue;
            default:
                return 0;
        }
    }


    public HashMap<String, HashMap<String, ArrayList<Integer>>> fillGrades(HashMap<String,
            ArrayList<String>> studentsGroups, int[] points) {

        HashMap<String, HashMap<String, ArrayList<Integer>>> grades = new HashMap<>();

        for (Map.Entry<String, ArrayList<String>> group: studentsGroups.entrySet()) {

            HashMap<String, ArrayList<Integer>> studentsOfTheGroup = new HashMap<>();

            for (String student : group.getValue()) {

                ArrayList<Integer> randGrades = new ArrayList<>();

                for (int point : points) {
                    randGrades.add(randomValue(point));
                }

                studentsOfTheGroup.put(student, randGrades);
            }
            grades.put(group.getKey(), studentsOfTheGroup);
        }

        grades.entrySet()
                .stream()
                .sorted(Map.Entry.<String, HashMap<String, ArrayList<Integer>>>comparingByKey())
                .forEach(System.out::println);

        return grades;
    }

    // TASK 3


    public HashMap<String, HashMap<String, Integer>> showGradesSum(HashMap<String,
            HashMap<String, ArrayList<Integer>>> grades) {

        HashMap<String, HashMap<String, Integer>> sumGrades = new HashMap<>();

        for (Map.Entry<String, HashMap<String, ArrayList<Integer>>> group : grades.entrySet()) {

            HashMap<String, Integer> studGrade = new HashMap<>();

            for (Map.Entry<String, ArrayList<Integer>> student : group.getValue().entrySet()) {

                int sum = 0;
                for (int i : student.getValue()) {
                    sum += i;
                }

                studGrade.put(student.getKey(), sum);
            }
            sumGrades.put(group.getKey(), studGrade);
        }

        sumGrades.entrySet()
                .stream()
                .sorted(Map.Entry.<String, HashMap<String, Integer>>comparingByKey())
                .forEach(System.out::println);

        return sumGrades;
    }

    // TASK 4


    public HashMap<String, Float> showAvgGradesInGroups(HashMap<String,
            HashMap<String, Integer>> sumGrades) {

        HashMap<String, Float> middle = new HashMap<>();

        for (Map.Entry<String, HashMap<String, Integer>> group: sumGrades.entrySet()) {

            float sumInGroup = 0;

            for (Map.Entry<String, Integer> student: group.getValue().entrySet()) {
                sumInGroup += student.getValue();
            }

            float avgGrade = (float) sumInGroup / group.getValue().size();
            middle.put(group.getKey(), avgGrade);
        }

        middle.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Float>comparingByKey())
                .forEach(System.out::println);

        return middle;
    }

    // TASK 5


    public HashMap<String, ArrayList<String>> showBestInGroups(HashMap<String,
            HashMap<String, Integer>> sumGrades) {

        HashMap<String, ArrayList<String>> top = new HashMap<>();

        for (Map.Entry<String, HashMap<String, Integer>> group: sumGrades.entrySet()) {

            ArrayList<String> bestStudents = new ArrayList<>();

            for (Map.Entry<String, Integer> student: group.getValue().entrySet()) {
                if (student.getValue() >= 60)
                    bestStudents.add(student.getKey());

            }

            top.put(group.getKey(), bestStudents);
        }

        top.entrySet()
                .stream()
                .sorted(Map.Entry.<String, ArrayList<String>>comparingByKey())
                .forEach(System.out::println);

        return top;
    }
}