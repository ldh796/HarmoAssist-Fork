package kr.sweetcase.harmoassist.statisticFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import kr.sweetcase.harmoassist.R
import kr.sweetcase.harmoassist.listMaterials.Music
import org.w3c.dom.Text

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StatisticInfoFragment.newInstance] factory method to
 * create an instance of this fragment.
 * TODO 여기에 미디 컨테이너도 포함시켜야 되는데 이건 백엔드 작업때
 */
class StatisticInfoFragment(val musicInfo : Music) : Fragment() {

    lateinit var titleText : TextView
    lateinit var chordText : TextView
    lateinit var signatureText : TextView
    lateinit var tempoText : TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val mainView : View = inflater.inflate(R.layout.fragment_statistic_info, container, false)

        titleText = mainView.findViewById(R.id.statistic_music_title)
        chordText = mainView.findViewById(R.id.statistic_music_chord)
        signatureText = mainView.findViewById(R.id.statistic_music_signature)
        tempoText = mainView.findViewById(R.id.statistic_music_tempo)

        titleText.text = musicInfo.title
        chordText.text = musicInfo.chord
        signatureText.text = musicInfo.timeSignature
        tempoText.text = musicInfo.tempo.toString()

        return mainView
    }
}
