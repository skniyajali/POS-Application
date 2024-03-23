package com.niyaj.cart_selected

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.niyaj.common.tags.CartOrderTestTags.CART_ORDER_NOT_AVAILABLE
import com.niyaj.common.tags.CartOrderTestTags.CREATE_NEW_CART_ORDER
import com.niyaj.common.tags.CartOrderTestTags.DELETE_CART_ORDER_ICON
import com.niyaj.common.tags.CartOrderTestTags.EDIT_CART_ORDER_ICON
import com.niyaj.common.tags.CartOrderTestTags.SELECT_CART_ORDER_SCREEN
import com.niyaj.designsystem.theme.SpaceMedium
import com.niyaj.designsystem.theme.SpaceSmall
import com.niyaj.designsystem.theme.Teal200
import com.niyaj.model.CartOrder
import com.niyaj.ui.components.ItemNotAvailable
import com.niyaj.ui.components.LoadingIndicator
import com.niyaj.ui.event.UiState
import com.niyaj.ui.util.BottomSheetWithCloseDialog
import com.niyaj.ui.util.Screens
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.spec.DestinationStyleBottomSheet

/**
 * Select Cart Order Screen
 * @author Sk Niyaj Ali
 * @param navController
 * @param viewModel
 */
@RootNavGraph(start = true)
@Destination(
    route = Screens.SELECT_ORDER_SCREEN,
    style = DestinationStyleBottomSheet::class
)
@Composable
fun SelectedCartOrderScreen(
    navController: NavController = rememberNavController(),
    viewModel: SelectedViewModel = hiltViewModel(),
    onClickCreateCartOrder: () -> Unit,
    onClickEditCartOrder: (String) -> Unit
) {
    val uiState = viewModel.cartOrders.collectAsStateWithLifecycle().value
    val selectedCartOrder = viewModel.selectedCartOrder.collectAsStateWithLifecycle().value

    BottomSheetWithCloseDialog(
        modifier = Modifier.fillMaxSize(),
        text = SELECT_CART_ORDER_SCREEN,
        onClosePressed = {
            navController.navigateUp()
        }
    ) {
        Crossfade(
            targetState = uiState,
            label = "Selected Order::State"
        ) { state ->
            when (state) {
                is UiState.Loading -> LoadingIndicator()

                is UiState.Empty -> {
                    ItemNotAvailable(
                        text = CART_ORDER_NOT_AVAILABLE,
                        buttonText = CREATE_NEW_CART_ORDER.uppercase(),
                        onClick = onClickCreateCartOrder
                    )
                }

                is UiState.Success -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = SpaceMedium, horizontal = SpaceSmall)
                    ) {
                        items(
                            items = state.data.asReversed(),
                            key = {
                                it.cartOrderId
                            }
                        ) {item ->
                            CartOrderBox(
                                cartOrder = item,
                                selectedOrderId = selectedCartOrder,
                                onClickCartOrder = {
                                    viewModel.selectCartOrder(it)
                                    navController.navigateUp()
                                },
                                onClickEdit = {
                                    viewModel.selectCartOrder(it)
                                    onClickEditCartOrder(it)
                                },
                                onClickDelete = {
                                    viewModel.deleteCartOrder(it)
                                    navController.navigateUp()
                                }
                            )

                            Spacer(modifier = Modifier.height(SpaceMedium))
                        }
                    }
                }
            }
        }
    }
}


/**
 * Cart Order Box Composable
 */
@Composable
fun CartOrderBox(
    cartOrder: CartOrder,
    selectedOrderId: String?,
    onClickCartOrder: (String) -> Unit,
    onClickEdit: (String) -> Unit,
    onClickDelete: (String) -> Unit,
    selectedColor: Color = MaterialTheme.colors.secondary,
    unselectedColor: Color = Teal200,
) {
    val color = if (selectedOrderId == cartOrder.cartOrderId) selectedColor else unselectedColor
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Row(
            modifier = Modifier
                .weight(2f)
                .fillMaxHeight()
                .clip(RoundedCornerShape(4.dp))
                .border(1.dp, color, RoundedCornerShape(4.dp))
                .clickable {
                    onClickCartOrder(cartOrder.cartOrderId)
                }
                .padding(SpaceSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(buildAnnotatedString {
                if (!cartOrder.address?.addressName.isNullOrEmpty()) {
                    withStyle(
                        style = SpanStyle(
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        cartOrder.address?.shortName?.let { append(it.uppercase()) }
                        append(" - ")
                    }
                }
                append(cartOrder.orderId)
            })

            Spacer(modifier = Modifier.width(SpaceMedium))
            Icon(
                imageVector = if (selectedOrderId == cartOrder.cartOrderId)
                    Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = color
            )
        }
        Spacer(modifier = Modifier.width(SpaceSmall))
        IconButton(
            onClick = {
                onClickEdit(cartOrder.cartOrderId)
            },
            modifier = Modifier
                .background(MaterialTheme.colors.primary, RoundedCornerShape(4.dp))
        ) {
            Icon(
                contentDescription = EDIT_CART_ORDER_ICON,
                imageVector = Icons.Default.Edit,
                tint = MaterialTheme.colors.onPrimary,
            )
        }
        Spacer(modifier = Modifier.width(SpaceSmall))
        IconButton(
            onClick = {
                onClickDelete(cartOrder.cartOrderId)
            },
            modifier = Modifier
                .background(MaterialTheme.colors.error, RoundedCornerShape(4.dp))
        ) {
            Icon(
                contentDescription = DELETE_CART_ORDER_ICON,
                imageVector = Icons.Default.Delete,
                tint = MaterialTheme.colors.onError
            )
        }
    }
}