package co.touchlab.droidcon.ui.session

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import co.touchlab.droidcon.ui.theme.Dimensions
import co.touchlab.droidcon.viewmodel.session.SessionDayComponent
import co.touchlab.droidcon.viewmodel.session.SessionDaysComponent
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.Direction
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.StackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.StackAnimator
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.isEnter
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import kotlinx.datetime.LocalDate

@Composable
internal fun SessionDaysView(days: SessionDaysComponent, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        val stack by days.stack.subscribeAsState()
        val selectedTabIndex = days.days.indexOfFirst { it.date == stack.active.instance.date }

        TabRow(selectedTabIndex = selectedTabIndex) {
            days.days.forEachIndexed { index, day ->
                Tab(selected = selectedTabIndex == index, onClick = { days.selectTab(date = day.date) }) {
                    Text(
                        text = day.title,
                        modifier = Modifier.padding(Dimensions.Padding.default),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }

        Children(
            stack = stack,
            modifier = Modifier.fillMaxSize(),
            animation = tabAnimation(indexOf = { date -> days.days.indexOfFirst { it.date == date } }),
        ) {
            SessionDayView(day = it.instance, modifier = Modifier.fillMaxSize())
        }
    }
}

@OptIn(ExperimentalDecomposeApi::class)
@Composable
private fun tabAnimation(indexOf: (LocalDate) -> Int): StackAnimation<Any, SessionDayComponent> =
    stackAnimation { child, otherChild, direction ->
        val index = indexOf(child.instance.date)
        val otherIndex = indexOf(otherChild.instance.date)
        val anim = slide()
        if ((index > otherIndex) == direction.isEnter) anim else anim.flipSide()
    }

@OptIn(ExperimentalDecomposeApi::class)
private fun StackAnimator.flipSide(): StackAnimator =
    StackAnimator { direction, onFinished, content ->
        invoke(
            direction = direction.flipSide(),
            onFinished = onFinished,
            content = content,
        )
    }

@Suppress("OPT_IN_USAGE")
private fun Direction.flipSide(): Direction =
    when (this) {
        Direction.ENTER_FRONT -> Direction.ENTER_BACK
        Direction.EXIT_FRONT -> Direction.EXIT_BACK
        Direction.ENTER_BACK -> Direction.ENTER_FRONT
        Direction.EXIT_BACK -> Direction.EXIT_FRONT
    }
