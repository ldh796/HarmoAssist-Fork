package kr.sweetcase.harmoassist

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ScrollView
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.bottomnavigation.BottomNavigationMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kr.sweetcase.harmoassist.listMaterials.Music
import kr.sweetcase.harmoassist.statisticFragments.StatisticFragment
import kr.sweetcase.harmoassist.statisticFragments.StatisticInfoFragment

class StatisticActivity : AppCompatActivity() {

    // 중복 생성 방지를 위해 열거형 클래스 선언
    enum class SelectedFragment {
        BASIC_INFO,
        STATISTIC,
        PROGRESS
    }
    private lateinit var statisticInfoFragment: StatisticInfoFragment
    private lateinit var statisticFragment: StatisticFragment

    lateinit var musicInfo : Music // 외부 액티비티로부터 받을 음악 정보.\
    lateinit var selectedFragment: SelectedFragment


    // 공유버튼
    lateinit var sharedBtn : FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)

        // TODO MusicInfo를 받으면 이 데이터를 토대로 DB에 접근해서 데이터를
        // 모조리 갖고 온 다음 Fragment에 전달해야함.
        musicInfo = intent.getSerializableExtra("statistics_info") as Music
        statisticInfoFragment = StatisticInfoFragment(musicInfo)
        statisticFragment = StatisticFragment(musicInfo)

        val navigation =
            findViewById<BottomNavigationView>(R.id.statistic_navi_bar)

        val fragmentManager = supportFragmentManager
        //transaction = fragmentManager.beginTransaction()
        supportFragmentManager.beginTransaction().replace(R.id.statistic_fragment, statisticInfoFragment).commit()
        selectedFragment = SelectedFragment.BASIC_INFO

        sharedBtn = findViewById(R.id.statistic_share_btn)


        navigation.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                // 기본 정보
                R.id.statistic_info_btn -> {
                    if(selectedFragment != SelectedFragment.BASIC_INFO) {
                        supportFragmentManager.beginTransaction().replace(R.id.statistic_fragment, statisticInfoFragment).commit()
                        selectedFragment = SelectedFragment.BASIC_INFO
                    }
                }
                // 분포도
                R.id.statistic_btn -> {
                    if(selectedFragment != SelectedFragment.STATISTIC) {
                        supportFragmentManager.beginTransaction().replace(R.id.statistic_fragment, statisticFragment).commit()
                        selectedFragment = SelectedFragment.STATISTIC
                    }
                }
                // 진행도
                R.id.statistic_progress_btn -> {
                    if(selectedFragment != SelectedFragment.PROGRESS) {
                        selectedFragment = SelectedFragment.PROGRESS
                    }
                }
                else -> {
                }
            }
            true
        }
        /* 공유 버튼 눌렀을 때 */
        sharedBtn.setOnClickListener {
            if(selectedFragment == SelectedFragment.STATISTIC) {
            }
        }
    }
}
