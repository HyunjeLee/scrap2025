package com.scrap2025.scrap2025.ui.common.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.scrap2025.scrap2025.ui.theme.MainColor
import com.scrap2025.scrap2025.ui.theme.MainColorDeep
import com.scrap2025.scrap2025.ui.theme.MainColorLight
import com.scrap2025.scrap2025.ui.theme.Scrap2025Theme
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.Date
import java.util.Locale

/**
 * CommonDateRangePickerDialog
 *
 * @param initialSelectedStartDateMillis 초기 선택될 시작 날짜
 * @param initialSelectedEndDateMillis 초기 선택될 종료 날짜
 * @param onDateSelected 날짜가 선택되었을 때 호출되는 콜백 (시작일, 종료일 전달)
 * @param onDismiss 다이얼로그가 닫힐 때 호출되는 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonDateRangePickerDialog(
    initialSelectedStartDateMillis: Long? = null,
    initialSelectedEndDateMillis: Long? = null,
    onDateSelected: (Long?, Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState =
        rememberDateRangePickerState(
            initialSelectedStartDateMillis = initialSelectedStartDateMillis,
            initialSelectedEndDateMillis = initialSelectedEndDateMillis
        )

    // 시작일과 종료일이 모두 선택되어야 확인 버튼 활성화
    val isConfirmEnabled =
        dateRangePickerState.selectedStartDateMillis != null && dateRangePickerState.selectedEndDateMillis != null

    DatePickerDialog(
        shape = RoundedCornerShape(15.dp),
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(
                        dateRangePickerState.selectedStartDateMillis,
                        dateRangePickerState.selectedEndDateMillis
                    )
                    onDismiss()
                },
                enabled = isConfirmEnabled
            ) {
                Text(
                    text = "확인",
                    color = if (isConfirmEnabled) MainColorDeep else Color.Gray,
                    fontWeight = if (isConfirmEnabled) FontWeight.Bold else null
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = "취소", color = Color.Gray) }
        },
        colors = DatePickerDefaults.colors(containerColor = Color.White)
    ) { CommonDateRangePickerContent(state = dateRangePickerState) }
}

/** CommonDateRangePickerContent 다이얼로그와 프리뷰에서 공통으로 사용하는 디자인 컨텐츠 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonDateRangePickerContent(state: DateRangePickerState) {
    // 날짜 기준점 계산 및 remember를 통한 캐싱
    val datePresets = remember {
        val now = LocalDate.now()

        listOf(
            DatePreset("오늘", now.toUtcMillis(), now.toUtcMillis()),
            DatePreset("최근 일주일", now.minusWeeks(1).toUtcMillis(), now.toUtcMillis()),
            DatePreset("최근 1년", now.minusYears(1).toUtcMillis(), now.toUtcMillis())
        )
    }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MainColorDeep)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            datePresets.forEach { preset ->
                val isSelected =
                    (state.selectedStartDateMillis == preset.startMillis) && (state.selectedEndDateMillis == preset.endMillis)

                Text(
                    modifier = Modifier.clickable {
                        state.setSelection(preset.startMillis, preset.endMillis)
                    },
                    text = preset.label,
                    color = MainColor,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
        }

        DateRangePicker(
            state = state,
            title = {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(MainColorDeep)
                            .padding(start = 24.dp)
                ) {
                    val year =
                        state.selectedStartDateMillis?.let { formatMillisToText(it, "yyyy년") }
                            ?: "연도"

                    Text(text = year, style = TextStyle(color = Color.White, fontSize = 16.sp))
                }
            },
            headline = {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .background(MainColorDeep)
                            .padding(
                                start = 24.dp,
                                end = 24.dp,
                                bottom = 20.dp,
                                top = 4.dp
                            )
                ) {
                    val startDateText =
                        state.selectedStartDateMillis?.let {
                            formatMillisToText(it, "M월 d일 (E)")
                        }
                    val endDateText =
                        state.selectedEndDateMillis?.let {
                            formatMillisToText(it, "M월 d일 (E)")
                        }

                    val headlineText =
                        when {
                            startDateText != null && endDateText != null ->
                                "$startDateText - $endDateText"

                            startDateText != null -> "$startDateText - 종료일 선택"
                            else -> "날짜 범위를 선택하세요"
                        }

                    Text(
                        text = headlineText,
                        style =
                            TextStyle(
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                    )
                }
            },
            showModeToggle = false, // 상단 Pen 아이콘 숨기기
            colors =
                DatePickerDefaults.colors(
                    containerColor = Color.White,
                    selectedDayContainerColor = MainColorDeep,
                    selectedDayContentColor = MainColor,
                    dayInSelectionRangeContainerColor = MainColorLight,
                    todayContentColor = Color.Black,
                    todayDateBorderColor = MainColorDeep
                )
        )
    }
}

/** 밀리초를 지정된 포맷의 텍스트로 변환하는 유틸리티 함수 */
private fun formatMillisToText(millis: Long, pattern: String): String {
    val instant = Instant.ofEpochMilli(millis)
    val date = Date.from(instant)
    return SimpleDateFormat(pattern, Locale.KOREA).format(date)
}

private fun LocalDate.toUtcMillis(): Long =
    this.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

private data class DatePreset(val label: String, val startMillis: Long, val endMillis: Long)


@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, name = "DatePicker Content Internal")
@Composable
fun DatePickerContentPreview() {
    val startMillis = LocalDateTime.of(2025, 12, 25, 0, 0).toInstant(ZoneOffset.UTC).toEpochMilli()
    val endMillis = LocalDateTime.of(2025, 12, 29, 0, 0).toInstant(ZoneOffset.UTC).toEpochMilli()

    Scrap2025Theme {
        val state =
            rememberDateRangePickerState(
                initialSelectedStartDateMillis = startMillis,
                initialSelectedEndDateMillis = endMillis
            )
        Surface(modifier = Modifier.padding(16.dp)) { CommonDateRangePickerContent(state = state) }
    }
}

@Preview(showBackground = true, name = "Full Dialog Preview")
@Composable
fun FullDatePickerDialogPreview() {
    Scrap2025Theme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CommonDateRangePickerDialog(onDateSelected = { _, _ -> }, onDismiss = {})
        }
    }
}
