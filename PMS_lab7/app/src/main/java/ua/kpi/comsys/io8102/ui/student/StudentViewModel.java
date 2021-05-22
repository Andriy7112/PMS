package ua.kpi.comsys.io8102.ui.student;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StudentViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public StudentViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Вигнанчук Андрій\nГрупа ІО-81\nЗК ІО-8102");
    }

    public LiveData<String> getText() {
        return mText;
    }
}