package com.kangaroo.nowchart.tools

import android.graphics.Path
import com.jeremyliao.liveeventbus.LiveEventBus
import com.kangaroo.nowchart.data.model.*
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

}