package com.jm.familyboard.mainFunction.familyInformation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.FirebaseDatabase
import com.jm.familyboard.R
import com.jm.familyboard.User
import com.jm.familyboard.datamodel.FamilyInformationResponse
import com.jm.familyboard.reusable.AppBar
import com.jm.familyboard.reusable.CompleteButton
import com.jm.familyboard.reusable.HowToUseColumn
import com.jm.familyboard.reusable.TextComposable

@Composable
fun FamilyInformationScreen(mainNavController: NavHostController) {
    val context = LocalContext.current
    val familyInformationArray = stringArrayResource(id = R.array.family_information_nav)
    val familyInformationViewModel = FamilyInformationViewModel()
    val familyCompositionList = remember { familyInformationViewModel.familyComposition }
    val currentNavController = rememberNavController()
    LaunchedEffect(true) { familyInformationViewModel.loadComposition(context) }
    NavHost(currentNavController, startDestination = familyInformationArray[1]) {
        composable(familyInformationArray[1]) {
            Column(modifier = Modifier
                .background(Color.White)
                .fillMaxSize()) {
                AppBar(User.uid == User.representativeUid || User.representativeUid.isEmpty(), familyInformationArray[0], if(User.uid == User.representativeUid || User.representativeUid.isEmpty()) R.drawable.ic_family_information else null, { currentNavController.navigate(familyInformationArray[3]) }) { mainNavController.popBackStack() }
                Column {
                    val text = remember { mutableStateOf("") }
                    when(User.uid == User.representativeUid) {
                        true -> {
                            text.value = stringResource(id = R.string.family_information_representative_is_me)
                        }
                        false -> {
                            if(User.representativeUid.isEmpty()) {
                                text.value = stringResource(id = R.string.family_information_no_representative)
                            } else {
                                text.value = stringResource(id = R.string.family_information_representative_exist_but_not_me)
                            }
                        }
                    }
                    HowToUseColumn(text = text.value)
                    Spacer(modifier = Modifier.height(10.dp))
                    familyCompositionList.value.forEach { info ->
                        Column(modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 10.dp)
                            .background(Color.White)
                        ) {
                            TextComposable(
                                text = if(info.uid == User.representativeUid) "${info.name} (${stringResource(id = R.string.representative)})" else info.name,
                                style = MaterialTheme.typography.labelLarge.copy(color = Color.Black),
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier
                                    .padding(start = 6.dp, top = 6.dp)
                                    .align(Alignment.Start)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            TextComposable(
                                text = if(info.uid == User.uid) stringResource(id = R.string.me) else info.roles,
                                style = MaterialTheme.typography.labelLarge.copy(color = Color.DarkGray),
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier
                                    .padding(bottom = 6.dp, end = 6.dp)
                                    .align(Alignment.End)
                            )
                        }
                        Divider()
                    }
                }
            }
        }
        composable(familyInformationArray[3]) {
            Column(modifier = Modifier
                .background(Color.White)
                .fillMaxSize()) {
                AppBar(false, familyInformationArray[2], null, {}) { currentNavController.popBackStack() }
                RepresentativeSelection(currentNavController, familyCompositionList)
            }
        }
    }
}

@Composable
fun RepresentativeSelection(currentNavController: NavHostController, familyComposition: MutableState<List<FamilyInformationResponse>>) {
    val uid = remember { mutableStateOf(User.representativeUid.ifEmpty { "" }) }
    val enabled = remember { mutableStateOf(false) }

    Column {
        Column(Modifier.weight(1f)) {
            val selectedOption = remember { mutableStateOf(uid) }
            val isSelect = remember { mutableStateOf(false) }

            enabled.value = selectedOption.value.value.isNotEmpty()

            Spacer(modifier = Modifier.height(20.dp))
            familyComposition.value.forEach { info ->
                isSelect.value = selectedOption.value.value == info.uid
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color.White)
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 20.dp)
                        .clickable(
                            interactionSource = MutableInteractionSource(),
                            indication = null
                        ) {
                            selectedOption.value.value = info.uid
                        }
                ) {
                    RadioButton(
                        selected = isSelect.value,
                        onClick = null,
                        colors = RadioButtonDefaults.colors(
                            selectedColor = Color.Blue.copy(0.2f),
                            unselectedColor = Color.LightGray,
                            disabledSelectedColor = Color.LightGray,
                            disabledUnselectedColor = Color.LightGray
                        ),
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    TextComposable(
                        text = if(User.uid == info.uid) " ${info.name}(${stringResource(id = R.string.me)})" else info.name,
                        style = MaterialTheme.typography.bodyMedium.copy(Color.Black),
                        fontWeight = FontWeight.Normal,
                        modifier = Modifier
                    )
                }
                Divider()
            }
        }
        CompleteButton(
            isEnable = enabled.value,
            color = Color(0xFFD59DAB),
            text = stringResource(id = R.string.selection),
            modifier = Modifier.fillMaxWidth()) {
            User.representativeUid = uid.value
            FirebaseDatabase.getInstance().getReference("real/service/${User.groupName}").child("representative").setValue(uid.value)
            currentNavController.popBackStack()
        }
    }
}