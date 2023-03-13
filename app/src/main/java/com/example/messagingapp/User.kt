package com.example.messagingapp

class User : java.io.Serializable{
    var email: String? = null
    var uid: String? = null
    var nick: String? = null

    constructor(){}

    constructor(email: String?, uid: String?, nick: String?){
        this.email = email
        this.uid = uid
        this.nick = nick
    }
}