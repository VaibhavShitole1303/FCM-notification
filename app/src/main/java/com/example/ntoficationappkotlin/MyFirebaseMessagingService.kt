package com.example.ntoficationappkotlin

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.ktx.remoteMessage

// channel id, channel name creating
const val channelId="notification_channel"
const val channelName="My_channel_notification"

class MyFirebaseMessagingService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.e("tag", token)
    }
         //Generate the notification
        fun generateNotification(title:String,message:String){
            //we make intent so that after clicking on the notification we can redirect to app
            val intent=Intent(this,MainActivity::class.java)
             // we add this flag= FLAG_ACTIVITY_CLEAR_TOP ,so that it will clear all tah activity on stack and
             //put this current activity(MainActivity after Clicking to notification) at the top
             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

             // we create pending intent if we have to use this intent in future
             // and we pass PendingIntent.FLAG_ONE_SHOT this flag so that it indicates we use this pending intent at once
             //i.e after clicking on notification this wil not use because  notification get destroy
             //it will use for next notification
             val pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_MUTABLE)

            // creating the notification using notificationBuilder
             var builder:NotificationCompat.Builder=NotificationCompat.Builder(applicationContext, channelId)
                 .setSmallIcon(R.drawable.ic_android_black_24dp)// setting Icon for notification
                 .setAutoCancel(true)// when user click on notification it cancel
                 .setVibrate(longArrayOf(1000,1000,1000,1000))// we set vibration this 1000 is vibrating time and relaxation time
                 .setOnlyAlertOnce(true)//this will give alert at once
                 .setContentIntent(pendingIntent)// we pass pending intent when notification is clicked

             //attaching notification-view with notification
             builder=builder.setContent(getRemoteView(title,message))

             // creating notification manager
             // with the help of getSystemService cast as notificationManager
             val notificationManager=getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

             //to check the clint android version is greater than android oreo or not
             // because most of the notification service are apply after oreo
             if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                 val notificationChannel=NotificationChannel(channelId, channelName,NotificationManager.IMPORTANCE_HIGH)
                 notificationManager.createNotificationChannel(notificationChannel)
             }

             notificationManager.notify(0,builder.build())




        }
        // this fun will add custom notification that we created with the notification view
        // creating getRemoteView fun so that we pass it to builder so we attach view with notification
        private fun getRemoteView(title: String, message: String): RemoteViews? {
            // we creating remote view with the help of packageName and notification layout

            val remoteViews=RemoteViews("com.example.ntoficationappkotlin",R.layout.notification)
            remoteViews.setTextViewText(R.id.title,title)
            remoteViews.setTextViewText(R.id.message,message)
            remoteViews.setImageViewResource(R.id.AppLogo,R.drawable.ic_baseline_call_24)
            return remoteViews
        }

       //Show the notification
         override fun onMessageReceived(message: RemoteMessage) {
         if (message.getNotification()!=null){
             // first we check the notification from firebase is not null
             //and then generateNotification
             generateNotification(message.notification!!.title!!,message.notification!!.body!!)

             val intent = Intent(applicationContext, MainActivity::class.java)
             intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NEW_TASK
             baseContext.startActivity(
                 intent
             )

         }

       }

}