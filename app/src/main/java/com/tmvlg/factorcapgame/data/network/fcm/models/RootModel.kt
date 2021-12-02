package com.tmvlg.factorcapgame.data.network.fcm.models

import com.fasterxml.jackson.annotation.JsonProperty

data class RootModel(
    @get:JsonProperty("to")
    var token: String,
    @get:JsonProperty("notification")
    var notification: NotificationModel,
    @get:JsonProperty("data")
    var data: DataModel,
    @get:JsonProperty("priority")
    var priority: String
)
