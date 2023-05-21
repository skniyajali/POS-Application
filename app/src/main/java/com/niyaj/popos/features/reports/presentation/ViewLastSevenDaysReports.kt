package com.niyaj.popos.features.reports.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowRightAlt
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.niyaj.popos.features.common.ui.theme.MediumGray
import com.niyaj.popos.features.common.ui.theme.SpaceSmall
import com.niyaj.popos.features.components.ItemNotAvailable
import com.niyaj.popos.features.components.StandardScaffold
import com.niyaj.popos.utils.toRupee
import com.ramcosta.composedestinations.annotation.Destination

@Destination
@Composable
fun ViewLastSevenDaysReports(
    navController: NavController,
    reportsViewModel: ReportsViewModel = hiltViewModel(),
) {
    val scaffoldState = rememberScaffoldState()

    val reportBarData = reportsViewModel.reportsBarData.collectAsState().value.reportBarData
    val reportBarIsLoading = reportsViewModel.reportsBarData.collectAsState().value.isLoading
    val reportBarError = reportsViewModel.reportsBarData.collectAsState().value.error

    StandardScaffold(
        navController = navController,
        scaffoldState = scaffoldState,
        showBackArrow = true,
        title = {
            Text(text = "Last 7 Days Reports")
        },
    ) {
        if(reportBarIsLoading){
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                CircularProgressIndicator()
            }
        } else if(reportBarError != null){
            ItemNotAvailable(
                text = reportBarError,
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(SpaceSmall)
            ) {
                items(reportBarData){ report ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(SpaceSmall),
                        elevation = 2.dp,
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(SpaceSmall),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Column{
                                Text(
                                    text = report.yValue.toString(),
                                    style = MaterialTheme.typography.body1,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(SpaceSmall))
                                Text(
                                    text = report.xValue.toString().substringBefore(".").toRupee,
                                    style = MaterialTheme.typography.body2,
                                    color = MediumGray
                                )
                            }

                            IconButton(
                                onClick = {}
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowRightAlt,
                                    contentDescription = "View Details",
                                    tint = MaterialTheme.colors.secondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(SpaceSmall))
                }
            }
        }
    }
}