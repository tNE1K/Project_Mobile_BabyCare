package com.example.project_mobile_babycare

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.androidplot.ui.widget.TextLabelWidget
import com.androidplot.xy.CatmullRomInterpolator
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.PanZoom
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.XYGraphWidget
import com.androidplot.xy.XYPlot
import java.text.DecimalFormat
import java.util.Arrays

class HeightWeightWhoActivity : AppCompatActivity() {
    private lateinit var linechartWeight: XYPlot
    private lateinit var linechartHeight: XYPlot
    private lateinit var linechartBmi: XYPlot
    private lateinit var btn_back: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_height_weight_who)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        linechartWeight = findViewById(R.id.linechart_weight)
        linechartHeight = findViewById(R.id.linechart_height)
        linechartBmi = findViewById(R.id.linechart_bmi)

        val months = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14)
        val weight_who = arrayOf(3.5, 4.0, 4.5, 5.0, 5.5, 6.0, 6.5, 7.0, 7.5, 8.0, 8.5, 9.0, 10.0, 11.0)
        val height_who = arrayOf(50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115)
        val bmi_who = arrayOf(14.0, 13.0, 12.0, 11.0, 10.0, 9.0, 8.0, 7.0, 6.0, 5.0, 4.0, 3.0, 2.0, 1.0)

        val series_weight_who = SimpleXYSeries(
            Arrays.asList(*months),
            Arrays.asList(*weight_who),
            "Cân nặng theo tháng"
        )
        val series_height_who = SimpleXYSeries(
            Arrays.asList(*months),
            Arrays.asList(*height_who),
            "Chiều cao theo tháng"
        )
        val series_bmi_who = SimpleXYSeries(
            Arrays.asList(*months),
            Arrays.asList(*bmi_who),
            "Chỉ số BMI theo tháng"
        )

        val series_WhoFormat = LineAndPointFormatter(Color.RED, null, null, null)
        series_WhoFormat.isLegendIconEnabled = false // Disable legend icon for this formatter
        series_WhoFormat.interpolationParams = CatmullRomInterpolator.Params(3000, CatmullRomInterpolator.Type.Uniform)
        series_WhoFormat.linePaint.strokeWidth = 5f // Độ dày của đường
        series_WhoFormat.vertexPaint.strokeWidth = 10f // Độ dày của điểm
        

        linechartWeight.addSeries(series_weight_who, series_WhoFormat)

        // Tắt chú thích toàn bộ đồ thị
        linechartWeight.layoutManager.remove(linechartWeight.legend)

        // Cài đặt các tiêu đề trục
        linechartWeight.setDomainLabel("Tháng")
        linechartWeight.setRangeLabel("Cân nặng (kg)")
//        linechartWeight.setTitle("Biểu đồ cân nặng")

        // Định dạng nhãn trục X thành số nguyên
        linechartWeight.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = DecimalFormat("0")
        linechartWeight.graph.getLineLabelStyle(XYGraphWidget.Edge.LEFT).format = DecimalFormat("0")

//        PanZoom.attach(linechartWeight)
        linechartWeight.outerLimits.set(1.0, 100.0, 0.0, 100.0)

        linechartHeight.addSeries(series_height_who, series_WhoFormat)

        // Tắt chú thích toàn bộ đồ thị
        linechartHeight.layoutManager.remove(linechartHeight.legend)

        // Cài đặt các tiêu đề trục
        linechartHeight.setDomainLabel("Tháng")
        linechartHeight.setRangeLabel("Chiều cao (cm)")
//        linechartHeight.title.text = "Biểu đồ chiều cao"

        // Định dạng nhãn trục X thành số nguyên
        linechartHeight.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = DecimalFormat("0")
        linechartHeight.graph.getLineLabelStyle(XYGraphWidget.Edge.LEFT).format = DecimalFormat("0")
//        PanZoom.attach(linechartHeight)
        linechartHeight.outerLimits.set(1.0, 100.0, 0.0, 100.0)

        linechartBmi.addSeries(series_bmi_who, series_WhoFormat)

        // Tắt chú thích toàn bộ đồ thị
        linechartBmi.layoutManager.remove(linechartBmi.legend)

        // Cài đặt các tiêu đề trục
        linechartBmi.setDomainLabel("Tháng")
        linechartBmi.setRangeLabel("BMI")
//        linechartBmi.title.text = "Biểu đồ BMI theo tháng"

        // Định dạng nhãn trục X thành số nguyên
        linechartBmi.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = DecimalFormat("0")
        linechartBmi.graph.getLineLabelStyle(XYGraphWidget.Edge.LEFT).format = DecimalFormat("0")
//        PanZoom.attach(linechartBmi)
        linechartBmi.outerLimits.set(1.0, 100.0, 0.0, 100.0)
    }
}