package org.main.socforfemale.ui.activity

import android.content.*
import android.os.IBinder
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_comment.*
import org.json.JSONObject
import org.main.socforfemale.R
import org.main.socforfemale.adapter.PostAudioGridAdapter
import org.main.socforfemale.base.Base
import org.main.socforfemale.base.BaseActivity
import org.main.socforfemale.bgservice.MusicController
import org.main.socforfemale.bgservice.MusicService
import org.main.socforfemale.connectors.MusicPlayerListener
import org.main.socforfemale.di.DaggerMVPComponent
import org.main.socforfemale.di.modules.ErrorConnModule
import org.main.socforfemale.di.modules.MVPModule
import org.main.socforfemale.di.modules.PresenterModule
import org.main.socforfemale.model.Audio
import org.main.socforfemale.model.Features
import org.main.socforfemale.mvp.Model
import org.main.socforfemale.mvp.Presenter
import org.main.socforfemale.mvp.Viewer
import org.main.socforfemale.pattern.builder.ErrorConnection
import org.main.socforfemale.resources.utils.Const
import org.main.socforfemale.resources.utils.log
import org.main.socforfemale.rest.Http
import org.main.socforfemale.ui.fragment.FeedFragment
import javax.inject.Inject

/**
 * Created by Sarvar on 09.09.2017.
 */
class PlaylistActivity : BaseActivity(),Viewer , MusicController.MediaPlayerControl, MusicPlayerListener {



    var drawingStartLocation               = 0
    var user  = Base.get.prefs.getUser()
    val model = Model()
    private var musicSrv: MusicService? = null
    private var songList: ArrayList<Audio>? = null

    //service
    private var playIntent: Intent? = null
    //binding
    private var musicBound = false

    //controller
    private var controller: MusicController? = null

    //activity and playback pause flags
    private var paused = false
    var playbackPaused = false
    @Inject
    lateinit var presenter:Presenter

    @Inject
    lateinit var errorConn: ErrorConnection

    lateinit var adapter:PostAudioGridAdapter

    override fun initProgress() {

    }

    override fun showProgress() {
        progressLay.visibility = View.VISIBLE
    }

    override fun hideProgress() {
        progressLay.visibility = View.GONE

    }

    override fun onSuccess(from: String, result: String) {
        log.d("from $from result $result")
        progressLay.visibility = View.GONE

       val features = Gson().fromJson(result,Features::class.java)

        adapter =  PostAudioGridAdapter(this,features.audios,this,model)
        emptyContainer.visibility = View.GONE
        setController()
        controller!!.show()
        list.adapter = adapter
    }

    override fun onFailure(from: String, message: String, erroCode: String) {
        log.d("from $from result $message errorCode $erroCode")
        progressLay.visibility = View.GONE

    }

    override fun getLayout(): Int  = R.layout.activity_playlist

    override fun initView() {
        DaggerMVPComponent
                .builder()
                .mVPModule(MVPModule(this, Model(), this))
                .presenterModule(PresenterModule())
                .errorConnModule(ErrorConnModule(this,true))
                .build()
                .inject(this)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.setTitle(resources.getString(R.string.my_playlist))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {

            onBackPressed()

        }
        Const.TAG = "PlaylistActivity"


        list.layoutManager = LinearLayoutManager(this)
        list.setHasFixedSize(true)


        errorConn.checkNetworkConnection(object : ErrorConnection.ErrorListener{
            override fun connected() {
                log.d("connected")

                val js = JSONObject()
                js.put("user_id", user.userId)
                js.put("session", user.session)
                presenter.requestAndResponse(js, Http.CMDS.GET_PLAYLIST)

            }

            override fun disconnected() {
                log.d("disconnected")


            }

        })
    }

    override fun activityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }

    val musicConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as MusicService.MusicBinder
            //get service
            musicSrv = binder.service
            //pass list
//            musicSrv!!.setList(songList)
            musicBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            musicBound = false
        }
    }

    override fun playClick(listSong: ArrayList<Audio>, position: Int) {
        try{

            if (musicSrv != null){
                log.d("PLAYIN SONG ${musicSrv!!.isPng}")
                if(musicSrv!!.isPng){

                    if (MusicService.PLAYING_SONG_URL == listSong.get(position).middlePath){
                        pause()
                    }else{
                        if(controller == null) setController()
                        controller!!.setLoading(true);

                        musicSrv!!.setList(listSong)
                        musicSrv!!.setSong(position)
                        musicSrv!!.playSong()

                        log.d("playbak is paused $playbackPaused")
                        if (playbackPaused){
                            setController()
                            playbackPaused = false
                        }
//                        controller!!.show()
                    }
                }else{
                    controller!!.setLoading(false);

                    if(MusicService.PLAY_STATUS == MusicService.PAUSED && MusicService.PLAYING_SONG_URL == listSong.get(position).middlePath){
                        start()
                    }else{

                        if(controller == null) setController()
                        controller!!.setLoading(true);

                        musicSrv!!.setList(listSong)
                        musicSrv!!.setSong(position)
                        musicSrv!!.playSong()
                        log.d("playbak is paused $playbackPaused")
                        if (playbackPaused){
                            setController()
                            playbackPaused = false
                        }
                    }
//                    controller!!.show()

                }


            }else{
                Toast.makeText(Base.get,Base.get.resources.getString(R.string.error_something), Toast.LENGTH_SHORT).show()
            }


            adapter.notifyDataSetChanged()



            FeedFragment.playedSongPosition = position
        }catch (e :Exception){

        }
    }


    override fun onStart() {
        super.onStart()
        if (playIntent == null) {

            playIntent = Intent(this, MusicService::class.java)
            this.bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE)
            this.startService(playIntent)

        }
    }

    override fun canPause(): Boolean {
        return true
    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun canSeekForward(): Boolean {
        return true
    }

    override fun getAudioSessionId(): Int {
        return 0
    }

    override fun getBufferPercentage(): Int {
        return 0
    }

    override fun getCurrentPosition(): Int {
        if (musicSrv != null && musicBound && musicSrv!!.isPng())
            return musicSrv!!.getPosn()
        else
            return 0
    }

    override fun getDuration(): Int {
        if (musicSrv != null && musicBound && musicSrv!!.isPng())
            return musicSrv!!.getDur()
        else
            return 0
    }

    override fun isPlaying(): Boolean {
        if (musicSrv != null && musicBound)
            return musicSrv!!.isPng()
        return false
    }

    override fun pause() {
        playbackPaused = true
        musicSrv!!.pausePlayer()
        if(controller != null) controller!!.setLoading(false);


        adapter.notifyDataSetChanged()

    }

    override fun seekTo(pos: Int) {
        musicSrv!!.seek(pos)

    }

    override fun start() {
        musicSrv!!.go()
        adapter.notifyDataSetChanged()

    }


    private fun setController() {
        if (controller == null){
            controller = MusicController(this)
            //set previous and next button listeners
            controller!!.setPrevNextListeners(View.OnClickListener { playNext() }, View.OnClickListener { playPrev() })
            //set and show
            controller!!.setMediaPlayer(this)
            controller!!.setAnchorView(findViewById(R.id.playlistRoot))
            controller!!.setEnabled(true)
        }
    }

    private fun playNext() {
        musicSrv!!.playNext()

        if (playbackPaused) {
            setController()
            playbackPaused = false
        }
        controller!!.setLoading(true);

        controller!!.show()
        try {

            adapter.notifyDataSetChanged()


        } catch (e: Exception) {

        }
    }

    private fun playPrev() {
        musicSrv!!.playPrev()
        if (playbackPaused) {
            setController()
            playbackPaused = false
        }
        controller!!.setLoading(true);

        controller!!.show()
        try {

            adapter.notifyDataSetChanged()


        } catch (e: Exception) {

        }

    }

    override fun onPause() {
        super.onPause()
        paused = true

    }

    override fun onResume() {
        super.onResume()
        log.d("onresume")
        LocalBroadcastManager.getInstance(this).registerReceiver(musicReceiver, IntentFilter(MusicService.TAG))
        if (paused) {
            setController()
            paused = false
        }
    }

    val musicReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (MusicService.CONTROL_PRESSED != -1){
                try {

                    adapter.notifyDataSetChanged()

                    MusicService.CONTROL_PRESSED = -1
                } catch (e: Exception) {

                }
            }
            if(controller != null) controller!!.show(0)
        }

    }

    override fun onStop() {
        if (controller != null){
            controller!!.hide()
        }
        super.onStop()
    }

    override fun onDestroy() {
        stopService(playIntent)
        musicSrv = null
        super.onDestroy()
    }
}