package com.ecm.mychatapp

class FriendlyMessage() {
    var id: String? = null
    var text: String? = null
    var name: String? = null

    constructor(id: String, text: String, name: String): this(){
        this.id = id
        this.name = name
        this.text = text

    }
}
//class FriendlyMessage() {
//    var id: String? = null
//    var text: String? = null
//    var name: String? = null
//    var senderId: String? = null
//
//    constructor(id: String, text: String, name: String, senderId: String): this() {
//        this.id = id
//        this.text = text
//        this.name = name
//        this.senderId = senderId
//    }
//}
