package com.kangaroo.nowchart.tools

import android.graphics.Path
import com.github.mikephil.charting.data.BarEntry
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kangaroo.nowchart.data.model.*
import com.kangaroo.nowchart.event.ClickEvent
import com.kangaroo.nowchart.event.RenEvent
import com.kangraoo.basektlib.app.SApplication
import com.kangraoo.basektlib.tools.HString
import com.kangraoo.basektlib.tools.UTime
import com.kangraoo.basektlib.tools.UUi
import com.kangraoo.basektlib.tools.json.HJson
import com.kangraoo.basektlib.tools.log.ULog
import com.kangraoo.basektlib.tools.store.MMKVStore
import com.kangraoo.basektlib.tools.store.MemoryStore

/**
 * @author shidawei
 * 创建日期：2021/6/21
 * 描述：
 */
const val USER:String = "user"
const val DATA:String = "data"
const val MESSAGE:String = "message"
const val HOTIM:String = "hotim"
const val toRen = "TO_REN"
const val toClick = "TO_CLICK"

object UStore {

    fun putUser(user : UserModel){
        MMKVStore.instance(HOTIM).put(USER,user)
    }

    fun getUser():UserModel? = MMKVStore.instance(HOTIM).get(USER,null,UserModel::class.java)

    fun clearUser(){
        MMKVStore.instance(HOTIM).remove(USER)
    }
    var set:HashSet<User> = HashSet<User>()

    @Synchronized
    fun putUser(user : User){
        ULog.o(user)
        if(!set.contains(user)){
            set.add(user)
            LiveEventBus.get<RenEvent>(toRen,RenEvent::class.java).post(RenEvent())
        }
    }

    fun getUserList():MutableList<User>{
        val list = ArrayList<User>()
        list.add(User(getUser()!!.name))
        set.forEach {
            list.add(it)
        }
        return list
    }

    var list: ArrayList<BarEntry> = ArrayList()

    fun clearClick() {
        list.clear()
        listUser.clear()
        click(UserClick(getUser()!!.name,1))
    }

    var listUser: ArrayList<String> = ArrayList()

    fun click(user : UserClick){
        if(!listUser.contains(user.name)){
            listUser.add(user.name)
            list.add(BarEntry(list.size.toFloat(),user.click.toFloat()))
        }else{
            list.forEach {
                if(listUser[it.x.toInt()]==user.name){
                    it.y = user.click.toFloat()
                }
            }
        }
        LiveEventBus.get<ClickEvent>(toClick,ClickEvent::class.java).post(ClickEvent())

    }

}