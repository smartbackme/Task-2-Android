package com.kangaroo.nowchart.data.model

import PASSWORD
import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.android.parcel.Parcelize

/**
 * @author shidawei
 * 创建日期：2021/6/18
 * 描述：
 */
@Parcelize
@JsonClass(generateAdapter = true)
data class User(val name:String):Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class UserModel(val name:String,val pass:String = PASSWORD,var token:String? = null):Parcelable

@Parcelize
@JsonClass(generateAdapter = true)
data class UserList(var username:String? = null,var extusername:String? = null,val user:Set<User>):Parcelable


@Parcelize
@JsonClass(generateAdapter = true)
data class UserClick(val name:String,val click: Int):Parcelable

