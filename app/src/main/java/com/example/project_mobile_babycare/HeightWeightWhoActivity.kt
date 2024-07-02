package com.example.project_mobile_babycare

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.androidplot.xy.CatmullRomInterpolator
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.XYGraphWidget
import com.androidplot.xy.XYPlot
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
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
        btn_back = findViewById(R.id.btnBack_hw_who)

        //lấy từ BD
        val months = arrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 ,18 ,19)

        val weight_who_min = arrayOf(3.5, 4.0, 4.5, 5.0, 5.5, 6.0, 6.5, 7.0, 7.5, 8.0, 8.5, 9.0)
        val weight_who_max = arrayOf(5.2, 5.7, 6.2, 6.7, 7.2, 7.7, 8.2, 8.7, 9.2, 9.7, 10.2, 10.7)
        val weight_who_avg: Array<Double> = Array(weight_who_min.size) { i ->
            (weight_who_min[i] + weight_who_max[i]) / 2
        }

        val weight_baby = arrayOf(3.0, 4.5, 5.0, 6.0, 5.5, 6.5, 8.0, 7.0, 9.0, 8.0, 8.5, 9.0)

        val height_who_min = arrayOf(50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100, 105)
        val height_who_max = arrayOf(60, 65, 70, 75, 80, 85, 90, 95, 100, 105, 110, 115)
        val height_who_avg: Array<Double> = Array(height_who_min.size) { i ->
            (height_who_min[i].toDouble() + height_who_max[i].toDouble()) / 2
        }

        val height_baby = arrayOf(52, 57, 62, 67, 72, 77, 82, 87, 92, 97, 102, 107)

        val bmi_who_min = arrayOf(14.0, 13.0, 12.0, 11.0, 10.0, 9.0, 8.0, 7.0, 6.0, 5.0, 4.0, 3.0)
        val bmi_who_max = arrayOf(16.0, 15.0, 14.0, 13.0, 12.0, 11.0, 10.0, 9.0, 8.0, 7.0, 6.0, 5.0)
        val bmi_who_avg: Array<Double> = Array(bmi_who_min.size) { i ->
            (bmi_who_min[i] + bmi_who_max[i]) / 2
        }

        val bmi_baby = arrayOf(15.0, 14.0, 13.0, 12.0, 11.0, 10.0, 9.0, 8.0, 7.0, 6.0, 5.0, 4.0)

        val countweight_baby = weight_baby.size

        val truncate_months = months.copyOfRange(0, countweight_baby)

        val truncateweight_who_min = weight_who_min.copyOfRange(0,countweight_baby)
        val truncateweight_who_max = weight_who_max.copyOfRange(0, countweight_baby)
        val truncateweight_who_avg = weight_who_avg.copyOfRange(0, countweight_baby)

        val truncateheight_who_min = height_who_min.copyOfRange(0, countweight_baby)
        val truncateheight_who_max = height_who_max.copyOfRange(0, countweight_baby)
        val truncateheight_who_avg = height_who_avg.copyOfRange(0, countweight_baby)

        val truncatebmi_who_min = bmi_who_min.copyOfRange(0, countweight_baby)
        val truncatebmi_who_max = bmi_who_max.copyOfRange(0, countweight_baby)
        val truncatebmi_who_avg = bmi_who_avg.copyOfRange(0, countweight_baby)


        val series_weight_who_min = SimpleXYSeries(
            Arrays.asList(*truncate_months),
            Arrays.asList(*truncateweight_who_min),
            "Cân nặng min theo tháng"
        )
        val series_weight_who_max = SimpleXYSeries(
            Arrays.asList(*truncate_months),
            Arrays.asList(*truncateweight_who_max),
            "Cân nặng max theo tháng"
        )
        val series_weight_who_avg = SimpleXYSeries(
            Arrays.asList(*truncate_months),
            Arrays.asList(*truncateweight_who_avg),
            "Cân nặng avg theo tháng"
        )


        val series_weight_baby = SimpleXYSeries(
            Arrays.asList(*truncate_months),
            Arrays.asList(*weight_baby),
            "Cân nặng của bé theo tháng"
        )

        val series_height_who_min = SimpleXYSeries(
            Arrays.asList(*truncate_months),
            Arrays.asList(*truncateheight_who_min),
            "Chiều cao min theo tháng"
        )
        val series_height_who_max = SimpleXYSeries(
            Arrays.asList(*truncate_months),
            Arrays.asList(*truncateheight_who_max),
            "Chiều cao max theo tháng"
        )
        val series_height_who_avg = SimpleXYSeries(
            Arrays.asList(*truncate_months),
            Arrays.asList(*truncateheight_who_avg),
            "Chiều cao avg theo tháng"
        )

        val series_height_baby = SimpleXYSeries(
            Arrays.asList(*truncate_months),
            Arrays.asList(*height_baby),
            "Chiều cao của bé theo tháng"
        )

        val series_bmi_who_min = SimpleXYSeries(
            Arrays.asList(*truncate_months),
            Arrays.asList(*truncatebmi_who_min),
            "Chỉ số BMI theo tháng"
        )
        val series_bmi_who_max = SimpleXYSeries(
            Arrays.asList(*truncate_months),
            Arrays.asList(*truncatebmi_who_max),
            "Chỉ số BMI theo tháng"
        )
        val series_bmi_who_avg = SimpleXYSeries(
            Arrays.asList(*truncate_months),
            Arrays.asList(*truncatebmi_who_avg),
            "Chỉ số BMI theo tháng"
        )


        val series_bmi_baby = SimpleXYSeries(
            Arrays.asList(*truncate_months),
            Arrays.asList(*bmi_baby),
            "Chỉ số BMI của bé theo tháng"
        )

        val series_WhoFormat = LineAndPointFormatter(Color.RED, null, null, null)
        series_WhoFormat.isLegendIconEnabled = false // Disable legend icon for this formatter
        series_WhoFormat.interpolationParams =
            CatmullRomInterpolator.Params(3000, CatmullRomInterpolator.Type.Uniform)
        series_WhoFormat.linePaint.strokeWidth = 5f // Độ dày của đường
        series_WhoFormat.vertexPaint.strokeWidth = 10f // Độ dày của điểm

        val series_WhoFormat_Avg = LineAndPointFormatter(Color.RED, null, null, null)
        series_WhoFormat_Avg.isLegendIconEnabled = false // Disable legend icon for this formatter
        series_WhoFormat_Avg.interpolationParams =
            CatmullRomInterpolator.Params(3000, CatmullRomInterpolator.Type.Uniform)
        series_WhoFormat_Avg.linePaint.strokeWidth = 5f // Độ dày của đường
        series_WhoFormat_Avg.vertexPaint.strokeWidth = 10f // Độ dày của điểm

        val dashPattern = floatArrayOf(20f, 10f)
        series_WhoFormat_Avg.linePaint.pathEffect = DashPathEffect(dashPattern, 0f)

        val series_BabyFormat = LineAndPointFormatter(Color.GREEN, null, null, null)
        series_BabyFormat.setLegendIconEnabled(false) // Disable legend icon for this formatter
        series_BabyFormat.interpolationParams =
            CatmullRomInterpolator.Params(3000, CatmullRomInterpolator.Type.Uniform)
        series_BabyFormat.linePaint.strokeWidth = 5f // Độ dày của đường
        series_BabyFormat.vertexPaint.strokeWidth = 10f // Độ dày của điểm

        linechartWeight.addSeries(series_weight_who_min, series_WhoFormat)
        linechartWeight.addSeries(series_weight_who_max, series_WhoFormat)
        linechartWeight.addSeries(series_weight_who_avg, series_WhoFormat_Avg)
        linechartWeight.addSeries(series_weight_baby, series_BabyFormat)

        // Tắt chú thích toàn bộ đồ thị
        linechartWeight.layoutManager.remove(linechartWeight.legend)

        // Cài đặt các tiêu đề trục
        linechartWeight.setDomainLabel("Tháng")
        linechartWeight.setRangeLabel("Cân nặng (kg)")
//        linechartWeight.setTitle("Biểu đồ cân nặng")

        // Định dạng nhãn trục X thành số nguyên
        linechartWeight.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format =
            DecimalFormat("0")
        linechartWeight.graph.getLineLabelStyle(XYGraphWidget.Edge.LEFT).format = DecimalFormat("0")

//        PanZoom.attach(linechartWeight)
        linechartWeight.outerLimits.set(1.0, 100.0, 0.0, 100.0)

        linechartHeight.addSeries(series_height_who_min, series_WhoFormat)
        linechartHeight.addSeries(series_height_who_max, series_WhoFormat)
        linechartHeight.addSeries(series_height_who_avg, series_WhoFormat_Avg)
        linechartHeight.addSeries(series_height_baby, series_BabyFormat)

        // Tắt chú thích toàn bộ đồ thị
        linechartHeight.layoutManager.remove(linechartHeight.legend)

        // Cài đặt các tiêu đề trục
        linechartHeight.setDomainLabel("Tháng")
        linechartHeight.setRangeLabel("Chiều cao (cm)")
//        linechartHeight.title.text = "Biểu đồ chiều cao"

        // Định dạng nhãn trục X thành số nguyên
        linechartHeight.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format =
            DecimalFormat("0")
        linechartHeight.graph.getLineLabelStyle(XYGraphWidget.Edge.LEFT).format = DecimalFormat("0")
//        PanZoom.attach(linechartHeight)
        linechartHeight.outerLimits.set(1.0, 100.0, 0.0, 100.0)

        linechartBmi.addSeries(series_bmi_who_min, series_WhoFormat)
        linechartBmi.addSeries(series_bmi_who_max, series_WhoFormat)
        linechartBmi.addSeries(series_bmi_who_avg, series_WhoFormat_Avg)
        linechartBmi.addSeries(series_bmi_baby, series_BabyFormat)

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