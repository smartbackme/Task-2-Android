package com.kangaroo.nowchart.ui.activity

import android.app.Activity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.gyf.immersionbar.ktx.immersionBar
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kangaroo.nowchart.R
import com.kangaroo.nowchart.data.model.UserClick
import com.kangaroo.nowchart.data.model.params.TokenPostParams
import com.kangaroo.nowchart.data.source.AppRepository
import com.kangaroo.nowchart.event.ClickEvent
import com.kangaroo.nowchart.event.RenEvent
import com.kangaroo.nowchart.tools.MqttUtil
import com.kangaroo.nowchart.tools.UStore
import com.kangaroo.nowchart.tools.UStore.listUser
import com.kangaroo.nowchart.tools.toClick
import com.kangaroo.nowchart.tools.toRen
import com.kangaroo.nowchart.ui.adapter.LineAdapter
import com.kangraoo.basektlib.data.DataResult
import com.kangraoo.basektlib.data.succeeded
import com.kangraoo.basektlib.tools.encryption.MessageDigestUtils
import com.kangraoo.basektlib.tools.json.HJson
import com.kangraoo.basektlib.tools.launcher.LibActivityLauncher
import com.kangraoo.basektlib.tools.log.ULog
import com.kangraoo.basektlib.tools.tip.Tip
import com.kangraoo.basektlib.ui.BActivity
import com.qdedu.baselibcommon.widget.toolsbar.CommonToolBarListener
import com.qdedu.baselibcommon.widget.toolsbar.CommonToolBarOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


/**
 * 自动生成：by WaTaNaBe on 2021-07-22 14:25
 * #首页#
 */
class MainActivity : BActivity(), OnChartValueSelectedListener, IAxisValueFormatter {

    companion object{

        fun startFrom(activity: Activity) {
            LibActivityLauncher.instance
                .launch(activity, MainActivity::class.java)
        }

    }

    override fun getLayoutId() = R.layout.activity_main


    private lateinit var adapter: LineAdapter
    var list: ArrayList<BarEntry> = ArrayList()
    override fun onViewCreated(savedInstanceState: Bundle?) {
        immersionBar {
            statusBarDarkFont(true)
            statusBarColor(R.color.theme)
        }

        val libToolBarOptions = CommonToolBarOptions()
        libToolBarOptions.titleString = "实时图表"
        libToolBarOptions.background = R.color.theme
        libToolBarOptions.titleColor = R.color.white
        libToolBarOptions.setNeedNavigate(false)
        setToolBar(R.id.toolbar, libToolBarOptions, object : CommonToolBarListener(){})


        bar.description.isEnabled = false
        bar.xAxis.position = XAxis.XAxisPosition.BOTTOM
        bar.axisRight.isEnabled = false
        bar.setScaleEnabled(false)
        bar.setOnChartValueSelectedListener(this)
        val xAxis: XAxis = bar.getXAxis()
        xAxis.setValueFormatter(this)
        launch {
            val user = UStore.getUser()
            showProgressingDialog("加载数据中")
            var data = AppRepository.instance.tokenPost(
                TokenPostParams(
                    username = user!!.name, password = MessageDigestUtils.sha1(
                        user!!.pass
                    )
                )
            )
            if(data.succeeded){
                if (data is DataResult.Success) {
                    user.token = data.data.access_token
                    UStore.putUser(user)
                    withContext(Dispatchers.IO){
                        MqttUtil.mqttService()
                        dismissProgressDialog()
                    }

//                    list.clear()
//                    listUser.add(user.name)
//                    list.add(BarEntry(1.0f,1.0f))
//                    barDataSet.notifyDataSetChanged()
                    UStore.click(UserClick(user.name,1))
                } else {
                    dismissProgressDialog()
                    showToastMsg(Tip.Error, "加载失败")
                }
            }else{
                dismissProgressDialog()
                showToastMsg(Tip.Error, "加载失败")
            }

        }
        adapter = LineAdapter(UStore.getUserList())
        var layoutManager = LinearLayoutManager(visitActivity())
        recycle.layoutManager = layoutManager
        recycle.adapter = adapter

        LiveEventBus.get<RenEvent>(toRen, RenEvent::class.java).observe(this, Observer {
            runOnUiThread {
                var list = UStore.getUserList()
                ULog.d(list)
                adapter.setNewInstance(list)
                adapter.notifyDataSetChanged()
            }
        })
        LiveEventBus.get<ClickEvent>(toClick, ClickEvent::class.java).observe(this, Observer {
            runOnUiThread {
//                barDataSet.clear()
//                UStore.list.forEach {
////                    barDataSet.addEntry(it)
//                    list.add(BarEntry(1.0f,1.0f))
//                    barDataSet.notifyDataSetChanged()
//                }
//                list.clear()
//                list.addAll(UStore.list)
//                barDataSet.notifyDataSetChanged()

                list.clear()
                list.addAll(UStore.list)
                val barDataSet = BarDataSet(list, "在线用户")

                val barData = BarData(barDataSet)
                bar.data = barData

                bar.notifyDataSetChanged()
                bar.invalidate()
            }
        })

        clear.setOnClickListener {
            MqttUtil.message(MqttUtil.clear,UStore.getUser()!!.name)
        }
    }

    override fun onDestroy() {
//        MqttUtil.message(MqttUtil.DeRen, UStore.getUser()?.name?:"")
        MqttUtil.unsubscribe()
        super.onDestroy()
    }

    override fun onNothingSelected() {
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        MqttUtil.message(MqttUtil.click,HJson.toJson(UserClick(listUser[e!!.x.toInt()],e.y.toInt()+1)))
    }

    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return listUser[value.toInt()]
    }


}
