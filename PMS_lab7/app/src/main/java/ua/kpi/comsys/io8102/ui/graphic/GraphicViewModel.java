package ua.kpi.comsys.io8102.ui.graphic;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GraphicViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GraphicViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Lab2");
    }

    public LiveData<String> getText() {
        return mText;
    }
}