package ua.kpi.comsys.io8102.ui.graphic;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.llollox.androidtoggleswitch.widgets.ToggleSwitch;

import java.util.ArrayList;
import java.util.List;

import ua.kpi.comsys.io8102.R;

public class GraphicFragment extends Fragment {
    private GraphView coordPlot;
    private PieChart pieChart;
    private static short togglePosition;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_graphic, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        coordPlot = (GraphView) view.findViewById(R.id.coordPlot);
        pieChart = (PieChart) view.findViewById(R.id.pieChart);
        ToggleSwitch toggleSwitch = (ToggleSwitch) view.findViewById(R.id.toggleGraphs);
        pieChart.setUsePercentValues(true);

        toggleSwitch.setOnChangeListener(new ToggleSwitch.OnChangeListener() {
            @Override
            public void onToggleSwitchChanged(int position) {
                if (position == 0) {
                    drawPlot();
                    togglePosition = 0;
                } else {
                    drawPieDiagram();
                    togglePosition = 1;
                }
            }
        });

        toggleSwitch.setCheckedPosition(togglePosition);
        if (togglePosition == 0) {
            drawPlot();
        } else {
            drawPieDiagram();
        }
    }

    public void drawPlot() {
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        pieChart.setVisibility(View.INVISIBLE);
        coordPlot.setVisibility(View.VISIBLE);
        double x, y;
        x = -4;
        int points = 800;

        for (int i = 0; i < points; i++) {
            x += 0.01;
            y = Math.log(x);
            series.appendData(new DataPoint(x, y), true, 600);
        }
        series.setAnimated(true);
        series.setColor(Color.BLUE);
        coordPlot.removeAllSeries();
        coordPlot.addSeries(series);

        coordPlot.getViewport().setXAxisBoundsManual(true);
        coordPlot.getViewport().setMinX(-4);
        coordPlot.getViewport().setMaxX(4);

        coordPlot.getViewport().setYAxisBoundsManual(true);
        coordPlot.getViewport().setMinY(-4);
        coordPlot.getViewport().setMaxY(4);

        coordPlot.getViewport().setScalable(true);
        coordPlot.getViewport().setScalableY(true);

        coordPlot.getGridLabelRenderer().setHorizontalLabelsVisible(false);
        coordPlot.getGridLabelRenderer().setVerticalLabelsVisible(false);
    }

    public void drawPieDiagram() {
        coordPlot.setVisibility(View.INVISIBLE);
        pieChart.setVisibility(View.VISIBLE);

        List<PieEntry> values = new ArrayList<>();

        values.add(new PieEntry(10f));
        values.add(new PieEntry(20f));
        values.add(new PieEntry(25f));
        values.add(new PieEntry(5f));
        values.add(new PieEntry(40f));

        PieDataSet pieDataSet = new PieDataSet(values, "Pie Chart");

        final int[] PIE_COLORS = {
                Color.rgb(255,255,0),
                Color.rgb(0,255,0),
                Color.rgb(0,0,255),
                Color.rgb(255,0,0),
                Color.rgb(0,191,255)};

        ArrayList<Integer> colors = new ArrayList<>();

        for(int i: PIE_COLORS) {
            colors.add(i);
        }

        pieDataSet.setColors(colors);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(false);
        pieData.setValueTextSize(0);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setRotationAngle(0);
        pieChart.animateY(800, Easing.EaseInOutExpo);
    }
}