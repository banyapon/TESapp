package com.daydev.tesapps

class DataModel {
    var title: String?=null
    var content: String?=null
    var thumbnail: String?=null
    var key:String?=null
    var location:String?=null
    var uid:String?=null

    constructor()
    constructor(
        title: String?,
        content: String?,
        thumbnail: String?,
        key: String?,
        location: String?,
        uid: String?
    ) {
        this.title = title
        this.content = content
        this.thumbnail = thumbnail
        this.key = key
        this.location = location
        this.uid = uid
    }

    fun toMap(): Map<String, Any> {
        val result = HashMap<String, Any>()
        result.put("title", title!!)
        result.put("content", content!!)
        result.put("thumbnail", thumbnail!!)
        result.put("location", location!!)
        result.put("key", key!!)
        result.put("uid", uid!!)
        return result
    }
}
