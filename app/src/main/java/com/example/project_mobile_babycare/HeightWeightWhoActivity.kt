package com.example.project_mobile_babycare

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.androidplot.xy.CatmullRomInterpolator
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.XYGraphWidget
import com.androidplot.xy.XYPlot
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.DecimalFormat


class HeightWeightWhoActivity : AppCompatActivity() {
    private lateinit var linechartWeight: XYPlot
    private lateinit var linechartHeight: XYPlot
    private lateinit var linechartBmi: XYPlot
    private lateinit var btn_back: Button
    val auth = Firebase.auth
    val user = auth.currentUser
    val db = Firebase.firestore
    lateinit var heightWeightWhoList: ArrayList<HeightWeightWho>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableFullscreenMode()
        setContentView(R.layout.activity_height_weight_who)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        heightWeightWhoList = intent.getParcelableArrayListExtra("heightweightwhoList")!!

        if (heightWeightWhoList != null) {
            for (item in heightWeightWhoList) {
                Log.d(
                    "HeightWeightWhoDataHWA",
                    "months: ${item.months}, height: ${item.height}, weight: ${item.weight}"
                )
            }
        } else {
            Log.e("HeightWeightWhoData", "heightweightwhoList is null")
        }


        linechartWeight = findViewById(R.id.linechart_weight)
        linechartHeight = findViewById(R.id.linechart_height)
        linechartBmi = findViewById(R.id.linechart_bmi)
        btn_back = findViewById(R.id.btnBack_hw_who)
        val intent = intent
        val userUID = intent.getStringExtra("userUID")
        val babyUID = intent.getStringExtra("babyUID")
        btn_back.setOnClickListener {
            val intent = Intent(this, HeightWeightActivity::class.java)
            intent.putExtra("userUID", userUID)
            intent.putExtra("babyUID", babyUID)
            startActivity(intent)
            finish()
        }


        fetchDataFromFirestore()

    }

    private fun fetchDataFromFirestore() {
//        Log.d("FirestoreData", "userUID: $userUID, babyUID: $babyUID")

        val docRefWho = db.collection("who_standards").document("male").collection("month")
        val whoData = mutableListOf<MonthData>()

        docRefWho.get().addOnSuccessListener { whoDocuments ->
            Log.d("FirestoreData", "WHO documents fetched: ${whoDocuments.size()}")
            for (document in whoDocuments) {
                Log.d("FirestoreData", "WHO document: ${document.data}")
                whoData.add(
                    MonthData(
                        month = document.getLong("month")!!.toInt(),
                        weightMin = document.getDouble("weight_min"),
                        weightMax = document.getDouble("weight_max"),
                        heightMin = document.getDouble("height_min"),
                        heightMax = document.getDouble("height_max")
                    )
                )
            }

            Log.d("FirestoreData", "WHO data processed: $whoData")

            if (heightWeightWhoList != null) {
                val babyData = mutableListOf<MonthData>()
                for (data in heightWeightWhoList) {
                    Log.d("IntentData", "Intent data: $data")
                    babyData.add(
                        MonthData(
                            month = data.months.toInt(),
                            weight = data.weight.toDouble(),
                            height = data.height.toDouble()
                        )
                    )
                }

                Log.d("IntentData", "Baby data processed: $babyData")

                val combinedData = combineData(whoData, babyData)
                updateCharts(combinedData)
            } else {
                Log.e("IntentError", "heightweightwhoList is null")
            }
        }


//        docRefBaby.get().addOnSuccessListener { babyDocuments ->
//            val babyData = mutableListOf<MonthData>()
//
//            for (document in babyDocuments) {
//                babyData.add(
//                    MonthData(
//                        month = document.getLong("month")!!.toInt(),
//                        weight = document.getDouble("weight"),
//                        height = document.getDouble("height")
//                    )
//                )
//            }
//        }
//
//        if (heightWeightWhoList != null) {
//            for (data in heightWeightWhoList) {
//                babyData.add(
//                    MonthData(
//                        month = data.months.toInt(),
//                        weight = data.weight.toDouble(),
//                        height = data.height.toDouble()
//                    )
//                )
//            }
//        } else {
//            Log.e("IntentError", "heightweightwhoList is null")
//        }
//        val combinedData = combineData(whoData, babyData)
//        updateCharts(combinedData)
    }

    private fun combineData(whoData: List<MonthData>, babyData: List<MonthData>): List<MonthData> {
        val combinedMonths =
            (whoData.map { it.month } + babyData.map { it.month }).distinct().sorted()
        val combinedData = mutableListOf<MonthData>()

        for (month in combinedMonths) {
            val whoMonthData = whoData.find { it.month == month }
            val babyMonthData = babyData.find { it.month == month }

            Log.d(
                "CombineData",
                "Month: $month, WHO Data: $whoMonthData, Baby Data: $babyMonthData"
            )

            combinedData.add(
                MonthData(
                    month = month,
                    weightMin = whoMonthData?.weightMin,
                    weightMax = whoMonthData?.weightMax,
                    weight = babyMonthData?.weight,
                    heightMin = whoMonthData?.heightMin,
                    heightMax = whoMonthData?.heightMax,
                    height = babyMonthData?.height,
                    bmiMin = if (whoMonthData != null) calculateBMI(
                        whoMonthData.weightMin,
                        whoMonthData.heightMin
                    ) else null,
                    bmiMax = if (whoMonthData != null) calculateBMI(
                        whoMonthData.weightMax,
                        whoMonthData.heightMax
                    ) else null,
                    bmi = if (babyMonthData != null && babyMonthData.weight != null && babyMonthData.height != null)
                        calculateBMI(babyMonthData.weight, babyMonthData.height) else null
                )
            )
        }

        Log.d("CombineData", "Combined Data: $combinedData")

        return combinedData
    }


    private fun updateCharts(data: List<MonthData>) {
        Log.d("UpdateCharts", "Received data: $data")

        // Lấy tháng tuổi lớn nhất của baby
        val maxBabyMonth = data.filter { it.weight != null && it.height != null && it.bmi != null }.maxOfOrNull { it.month } ?: 0

        // Lọc dữ liệu chỉ đến tháng tuổi lớn nhất của baby
        val filteredData = data.filter { it.month <= maxBabyMonth }

        // Xử lý dữ liệu để loại bỏ các giá trị null
        val validMonths = filteredData.filter { it.weight != null && it.height != null && it.bmi != null }.map { it.month }
        val validWeightBaby = filteredData.filter { it.weight != null }.map { it.weight }
        val validHeightBaby = filteredData.filter { it.height != null }.map { it.height }
        val validBmiBaby = filteredData.filter { it.bmi != null }.map { it.bmi }

        val months = filteredData.map { it.month }.toTypedArray()
        val weightWhoMin = filteredData.map { it.weightMin }.toTypedArray()
        val weightWhoMax = filteredData.map { it.weightMax }.toTypedArray()
        val weightWhoAvg = filteredData.map {
            if (it.weightMin != null && it.weightMax != null)
                (it.weightMin + it.weightMax) / 2
            else
                null
        }.toTypedArray()
        val heightWhoMin = filteredData.map { it.heightMin }.toTypedArray()
        val heightWhoMax = filteredData.map { it.heightMax }.toTypedArray()
        val heightWhoAvg = filteredData.map {
            if (it.heightMin != null && it.heightMax != null)
                (it.heightMin + it.heightMax) / 2
            else
                null
        }.toTypedArray()
        val weightBaby = filteredData.map { it.weight }.toTypedArray()
        val heightBaby = filteredData.map { it.height }.toTypedArray()
        val bmiWhoMin = filteredData.map { it.bmiMin }.toTypedArray()
        val bmiWhoMax = filteredData.map { it.bmiMax }.toTypedArray()
        val bmiWhoAvg = filteredData.map {
            if (it.bmiMin != null && it.bmiMax != null)
                (it.bmiMin + it.bmiMax) / 2
            else
                null
        }.toTypedArray()
        val bmiBaby = filteredData.map { it.bmi }.toTypedArray()

        Log.d("MyActivity", "months: ${months.joinToString()}")
        Log.d("MyActivity", "weightWhoMin: ${weightWhoMin.joinToString()}")
        Log.d("MyActivity", "weightWhoMax: ${weightWhoMax.joinToString()}")
        Log.d("MyActivity", "weightWhoAvg: ${weightWhoAvg.joinToString()}")
        Log.d("MyActivity", "heightWhoMin: ${heightWhoMin.joinToString()}")
        Log.d("MyActivity", "heightWhoMax: ${heightWhoMax.joinToString()}")
        Log.d("MyActivity", "heightWhoAvg: ${heightWhoAvg.joinToString()}")
        Log.d("MyActivity", "weightBaby: ${weightBaby.joinToString()}")
        Log.d("MyActivity", "heightBaby: ${heightBaby.joinToString()}")
        Log.d("MyActivity", "bmiWhoMin: ${bmiWhoMin.joinToString()}")
        Log.d("MyActivity", "bmiWhoMax: ${bmiWhoMax.joinToString()}")
        Log.d("MyActivity", "bmiWhoAvg: ${bmiWhoAvg.joinToString()}")
        Log.d("MyActivity", "bmiBaby: ${bmiBaby.joinToString()}")

        val seriesWeightWhoMin = SimpleXYSeries(months.toList(), weightWhoMin.toList(), "Cân nặng min theo tháng")
        val seriesWeightWhoMax = SimpleXYSeries(months.toList(), weightWhoMax.toList(), "Cân nặng max theo tháng")
        val seriesWeightWhoAvg = SimpleXYSeries(months.toList(), weightWhoAvg.toList(), "Cân nặng avg theo tháng")
        val seriesWeightBaby = SimpleXYSeries(validMonths, validWeightBaby, "Cân nặng của bé theo tháng")

        val seriesHeightWhoMin = SimpleXYSeries(months.toList(), heightWhoMin.toList(), "Chiều cao min theo tháng")
        val seriesHeightWhoMax = SimpleXYSeries(months.toList(), heightWhoMax.toList(), "Chiều cao max theo tháng")
        val seriesHeightWhoAvg = SimpleXYSeries(months.toList(), heightWhoAvg.toList(), "Chiều cao avg theo tháng")
        val seriesHeightBaby = SimpleXYSeries(validMonths, validHeightBaby, "Chiều cao của bé theo tháng")

        val seriesBmiWhoMin = SimpleXYSeries(months.toList(), bmiWhoMin.toList(), "BMI min theo tháng")
        val seriesBmiWhoMax = SimpleXYSeries(months.toList(), bmiWhoMax.toList(), "BMI max theo tháng")
        val seriesBmiWhoAvg = SimpleXYSeries(months.toList(), bmiWhoAvg.toList(), "BMI avg theo tháng")
        val seriesBmiBaby = SimpleXYSeries(validMonths, validBmiBaby, "BMI của bé theo tháng")

        val formatterWho = LineAndPointFormatter(Color.RED, null, null, null).apply {
            linePaint.strokeWidth = 5f
            isLegendIconEnabled = false
            interpolationParams = CatmullRomInterpolator.Params(3000, CatmullRomInterpolator.Type.Uniform)
        }

        val formatterWhoAvg = LineAndPointFormatter(Color.GREEN, null, null, null)
        val formatterBaby = LineAndPointFormatter(Color.MAGENTA, Color.BLUE, null, null)

        formatterWhoAvg.linePaint.strokeWidth = 5f
        formatterBaby.linePaint.strokeWidth = 5f

        formatterWhoAvg.linePaint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)

        linechartWeight.clear()
        linechartWeight.apply {
            addSeries(seriesWeightWhoMin, formatterWho)
            addSeries(seriesWeightWhoMax, formatterWho)
            addSeries(seriesWeightWhoAvg, formatterWhoAvg)
            addSeries(seriesWeightBaby, formatterBaby)

            setDomainLabel("Tháng")
            setRangeLabel("Cân nặng (kg)")

            layoutManager.remove(linechartWeight.legend)
            graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = DecimalFormat("0")
            graph.getLineLabelStyle(XYGraphWidget.Edge.LEFT).format = DecimalFormat("0")
            outerLimits.set(0.0, 100.0, 0.0, 150.0)
        }

        linechartHeight.clear()
        linechartHeight.apply {
            addSeries(seriesHeightWhoMin, formatterWho)
            addSeries(seriesHeightWhoMax, formatterWho)
            addSeries(seriesHeightWhoAvg, formatterWhoAvg)
            addSeries(seriesHeightBaby, formatterBaby)

            setDomainLabel("Tháng")
            setRangeLabel("Chiều cao (cm)")

            layoutManager.remove(linechartHeight.legend)
            graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = DecimalFormat("0")
            graph.getLineLabelStyle(XYGraphWidget.Edge.LEFT).format = DecimalFormat("0")
            outerLimits.set(0.0, 100.0, 0.0, 150.0)
        }

        linechartBmi.clear()
        linechartBmi.apply {
            addSeries(seriesBmiWhoMin, formatterWho)
            addSeries(seriesBmiWhoMax, formatterWho)
            addSeries(seriesBmiWhoAvg, formatterWhoAvg)
            addSeries(seriesBmiBaby, formatterBaby)

            setDomainLabel("Tháng")
            setRangeLabel("BMI")

            layoutManager.remove(linechartBmi.legend)
            graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = DecimalFormat("0")
            graph.getLineLabelStyle(XYGraphWidget.Edge.LEFT).format = DecimalFormat("0")
            outerLimits.set(0.0, 100.0, 0.0, 150.0)
        }

        linechartWeight.redraw()
        linechartHeight.redraw()
        linechartBmi.redraw()
    }




    private fun calculateBMI(weight: Double?, height: Double?): Double? {
        return if (weight != null && height != null) {
            weight * 10000 / (height * height)
        } else {
            null
        }
    }
    private fun Activity.enableFullscreenMode() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        // Hide the navigation and status bars
        windowInsetsController.let {
            it.hide(WindowInsetsCompat.Type.systemBars())
            it.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}