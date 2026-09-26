package com.yunwu.dragonmecha

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.SportsKabaddi
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class Dragon(val name: String, val role: String, val hp: Int, val attack: Int, val defense: Int, val speed: Int, val emoji: String)
data class Part(val name: String, val slot: String, val attack: Int = 0, val defense: Int = 0, val hp: Int = 0, val speed: Int = 0, val weight: Int = 0, val emoji: String)
data class Build(val head: Part, val body: Part, val leftArm: Part, val rightArm: Part, val legs: Part) {
    val attack get() = head.attack + body.attack + leftArm.attack + rightArm.attack + legs.attack
    val defense get() = head.defense + body.defense + leftArm.defense + rightArm.defense + legs.defense
    val hp get() = head.hp + body.hp + leftArm.hp + rightArm.hp + legs.hp
    val speed get() = head.speed + body.speed + leftArm.speed + rightArm.speed + legs.speed
}
enum class Page { HOME, DRAGONS, BUILD, BATTLE, PARTS }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { DragonMechaApp(applicationContext) }
    }
}

@Composable
fun DragonMechaApp(context: Context) {
    val dragons = remember { listOf(
        Dragon("赤焰龙", "强攻型", 120, 80, 45, 55, "🐉"),
        Dragon("鲨龙", "重装型", 160, 58, 90, 35, "🦈"),
        Dragon("雷翼龙", "机动型", 105, 68, 40, 100, "⚡"),
        Dragon("岩甲龙", "防御型", 190, 52, 110, 28, "🪨")
    ) }
    val heads = remember { listOf(
        Part("标准战斗头", "头部", defense = 10, speed = 5, emoji = "◈"),
        Part("重装装甲头", "头部", defense = 25, hp = 20, speed = -5, weight = 15, emoji = "⬢"),
        Part("高速侦察头", "头部", attack = 8, speed = 20, emoji = "◇"),
        Part("能量感知头", "头部", attack = 15, defense = 12, speed = 10, emoji = "◆")
    ) }
    val bodies = remember { listOf(
        Part("轻型机体", "身体", attack = 10, defense = 15, hp = 20, speed = 15, emoji = "▣"),
        Part("重装机体", "身体", attack = 15, defense = 40, hp = 60, speed = -15, weight = 30, emoji = "▰"),
        Part("超重装机体", "身体", attack = 30, defense = 70, hp = 100, speed = -30, weight = 60, emoji = "▣"),
        Part("突击核心", "身体", attack = 45, defense = 25, hp = 45, speed = 25, weight = 15, emoji = "◆")
    ) }
    val leftArms = remember { listOf(
        Part("机械手", "左臂", attack = 15, defense = 5, emoji = "L"),
        Part("能量炮", "左臂", attack = 55, speed = -5, weight = 20, emoji = "◉"),
        Part("液压拳", "左臂", attack = 80, speed = -10, weight = 35, emoji = "✊"),
        Part("护盾臂", "左臂", attack = 20, defense = 35, hp = 20, speed = -5, emoji = "▱")
    ) }
    val rightArms = remember { listOf(
        Part("机械手", "右臂", attack = 15, defense = 5, emoji = "R"),
        Part("加特林", "右臂", attack = 65, speed = -8, weight = 25, emoji = "◎"),
        Part("巨型液压拳", "右臂", attack = 110, speed = -15, weight = 45, emoji = "✊"),
        Part("重型能量炮", "右臂", attack = 90, defense = 5, speed = -12, weight = 30, emoji = "◉")
    ) }
    val legs = remember { listOf(
        Part("标准机甲腿", "腿部", defense = 15, speed = 15, emoji = "Ⅱ"),
        Part("重装腿", "腿部", defense = 40, hp = 35, speed = -10, weight = 25, emoji = "▥"),
        Part("履带底盘", "腿部", defense = 55, hp = 50, speed = -25, weight = 45, emoji = "▰"),
        Part("推进腿", "腿部", attack = 20, defense = 10, hp = 15, speed = 40, weight = 10, emoji = "⇈")
    ) }

    val prefs = remember { context.getSharedPreferences("dragon_mecha_v11", Context.MODE_PRIVATE) }
    var page by remember { mutableStateOf(Page.HOME) }
    var selectedDragon by remember { mutableStateOf(dragons[prefs.getInt("dragon", 0).coerceIn(dragons.indices)]) }
    var selectedHead by remember { mutableStateOf(heads[prefs.getInt("head", 0).coerceIn(heads.indices)]) }
    var selectedBody by remember { mutableStateOf(bodies[prefs.getInt("body", 1).coerceIn(bodies.indices)]) }
    var selectedLeft by remember { mutableStateOf(leftArms[prefs.getInt("left", 0).coerceIn(leftArms.indices)]) }
    var selectedRight by remember { mutableStateOf(rightArms[prefs.getInt("right", 2).coerceIn(rightArms.indices)]) }
    var selectedLegs by remember { mutableStateOf(legs[prefs.getInt("legs", 0).coerceIn(legs.indices)]) }
    var wins by remember { mutableStateOf(prefs.getInt("wins", 0)) }
    var coins by remember { mutableStateOf(prefs.getInt("coins", 100)) }
    val build = Build(selectedHead, selectedBody, selectedLeft, selectedRight, selectedLegs)

    fun save() = prefs.edit()
        .putInt("dragon", dragons.indexOf(selectedDragon)).putInt("head", heads.indexOf(selectedHead))
        .putInt("body", bodies.indexOf(selectedBody)).putInt("left", leftArms.indexOf(selectedLeft))
        .putInt("right", rightArms.indexOf(selectedRight)).putInt("legs", legs.indexOf(selectedLegs))
        .putInt("wins", wins).putInt("coins", coins).apply()

    MaterialTheme(colorScheme = lightColorScheme()) {
        Scaffold(
            topBar = { Row(Modifier.fillMaxWidth().background(Color(0xFF0B1020)).padding(horizontal = 18.dp, vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("⚙", fontSize = 25.sp); Spacer(Modifier.width(10.dp)); Column {
                    Text("龙兽机甲工坊", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Text("DRAGON MECHA WORKSHOP · V1.1", color = Color(0xFF94A3B8), fontSize = 10.sp)
                }
            } },
            bottomBar = { NavigationBar {
                NavigationBarItem(page == Page.HOME, { page = Page.HOME }, icon = { Icon(Icons.Default.Home, null) }, label = { Text("工坊") })
                NavigationBarItem(page == Page.DRAGONS, { page = Page.DRAGONS }, icon = { Icon(Icons.Default.Favorite, null) }, label = { Text("龙兽人") })
                NavigationBarItem(page == Page.BUILD, { page = Page.BUILD }, icon = { Icon(Icons.Default.Build, null) }, label = { Text("组装") })
                NavigationBarItem(page == Page.BATTLE, { page = Page.BATTLE }, icon = { Icon(Icons.Default.SportsKabaddi, null) }, label = { Text("战斗") })
                NavigationBarItem(page == Page.PARTS, { page = Page.PARTS }, icon = { Icon(Icons.Default.Inventory, null) }, label = { Text("仓库") })
            } }
        ) { padding -> AnimatedContent(targetState = page, modifier = Modifier.fillMaxSize().padding(padding), transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "page") { current ->
            when (current) {
                Page.HOME -> HomePage(selectedDragon, build, wins, coins, { page = Page.BUILD }, { page = Page.BATTLE })
                Page.DRAGONS -> DragonPage(dragons, selectedDragon) { selectedDragon = it; save(); page = Page.BUILD }
                Page.BUILD -> BuildPage(selectedDragon, build, heads, bodies, leftArms, rightArms, legs,
                    { selectedHead = it; save() }, { selectedBody = it; save() }, { selectedLeft = it; save() }, { selectedRight = it; save() }, { selectedLegs = it; save() })
                Page.BATTLE -> BattlePage(selectedDragon, build, onWin = { wins++; coins += 30; save() }, onLose = { save() })
                Page.PARTS -> PartsPage(heads + bodies + leftArms + rightArms + legs)
            }
        } }
    }
}

@Composable
fun HomePage(dragon: Dragon, build: Build, wins: Int, coins: Int, onBuild: () -> Unit, onBattle: () -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)), shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.fillMaxWidth().padding(24.dp)) {
                Text("当前驾驶者", color = Color(0xFF94A3B8)); Spacer(Modifier.height(8.dp))
                Text("${dragon.emoji} ${dragon.name}", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
                Text(dragon.role, color = Color(0xFF38BDF8)); Spacer(Modifier.height(18.dp))
                Text("自定义机甲", color = Color(0xFFCBD5E1)); Text("${build.body.name} · ${build.rightArm.name}", color = Color.White, fontSize = 19.sp)
            }
        } }
        item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            MiniStat("HP", dragon.hp + build.hp, Modifier.weight(1f)); MiniStat("攻击", dragon.attack + build.attack, Modifier.weight(1f)); MiniStat("防御", dragon.defense + build.defense, Modifier.weight(1f)); MiniStat("速度", dragon.speed + build.speed, Modifier.weight(1f))
        } }
        item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) { MiniStat("胜场", wins, Modifier.weight(1f)); MiniStat("金币", coins, Modifier.weight(1f)) } }
        item { Button(onClick = onBuild, modifier = Modifier.fillMaxWidth().height(55.dp)) { Text("进入机甲组装") } }
        item { Button(onClick = onBattle, modifier = Modifier.fillMaxWidth().height(55.dp)) { Text("进入训练战") } }
        item { Text("V1.1 · 4名龙兽人 · 20个基础部件 · 1024种基础组合 · 自动保存", color = Color.Gray, fontSize = 12.sp) }
    }
}

@Composable fun MiniStat(name: String, value: Int, modifier: Modifier) { Card(modifier) { Column(Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(name, color = Color.Gray, fontSize = 11.sp); Text(value.coerceAtLeast(0).toString(), fontWeight = FontWeight.Bold, fontSize = 17.sp) } } }

@Composable fun DragonPage(dragons: List<Dragon>, selected: Dragon, onSelect: (Dragon) -> Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { item { Text("龙兽人", fontSize = 28.sp, fontWeight = FontWeight.Bold); Text("选择一名龙兽人作为你的机甲驾驶者", color = Color.Gray) }; items(dragons) { dragon -> Card(Modifier.fillMaxWidth().clickable { onSelect(dragon) }, shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = if (dragon == selected) Color(0xFFE0F2FE) else Color.White)) { Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) { Text(dragon.emoji, fontSize = 40.sp); Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) { Text(dragon.name, fontSize = 21.sp, fontWeight = FontWeight.Bold); Text(dragon.role, color = Color(0xFF0284C7)); Text("HP ${dragon.hp} · 攻击 ${dragon.attack} · 防御 ${dragon.defense} · 速度 ${dragon.speed}", fontSize = 12.sp, color = Color.Gray) } } } } }
}

@Composable fun BuildPage(dragon: Dragon, build: Build, heads: List<Part>, bodies: List<Part>, lefts: List<Part>, rights: List<Part>, legs: List<Part>, onHead: (Part)->Unit, onBody:(Part)->Unit, onLeft:(Part)->Unit, onRight:(Part)->Unit, onLegs:(Part)->Unit) {
    LazyColumn(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) { item { Text("机甲组装", fontSize = 28.sp, fontWeight = FontWeight.Bold); Text("${dragon.name} · 自由组合", color = Color.Gray) }; item { BuildPreview(dragon, build) }; item { PartPicker("头部", heads, build.head, onHead) }; item { PartPicker("身体", bodies, build.body, onBody) }; item { PartPicker("左臂", lefts, build.leftArm, onLeft) }; item { PartPicker("右臂", rights, build.rightArm, onRight) }; item { PartPicker("腿部", legs, build.legs, onLegs) } }
}

@Composable fun BuildPreview(dragon: Dragon, build: Build) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF111827)), shape = RoundedCornerShape(24.dp)) { Column(Modifier.fillMaxWidth().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text("${dragon.emoji}  🤖", fontSize = 58.sp); Text("${dragon.name}·${build.body.name}", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold); Text("${build.head.name} / ${build.leftArm.name} / ${build.rightArm.name} / ${build.legs.name}", color = Color(0xFF94A3B8), fontSize = 12.sp); Spacer(Modifier.height(12.dp)); Text("HP ${dragon.hp + build.hp}   攻击 ${dragon.attack + build.attack}", color = Color.White); Text("防御 ${dragon.defense + build.defense}   速度 ${dragon.speed + build.speed}", color = Color(0xFF38BDF8)) } } }

@Composable fun PartPicker(title: String, parts: List<Part>, selected: Part, onSelect: (Part)->Unit) { Column { Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(6.dp)); parts.forEach { part -> Card(Modifier.fillMaxWidth().padding(vertical = 3.dp).clickable { onSelect(part) }, shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = if (part == selected) Color(0xFFE0F2FE) else Color.White)) { Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) { Text(part.emoji, fontSize = 25.sp, modifier = Modifier.width(40.dp)); Column(Modifier.weight(1f)) { Text(part.name, fontWeight = FontWeight.Bold); Text("攻击 ${signed(part.attack)}  防御 ${signed(part.defense)}  HP ${signed(part.hp)}  速度 ${signed(part.speed)}", color = Color.Gray, fontSize = 11.sp) }; if (part == selected) Text("已装备", color = Color(0xFF0284C7), fontSize = 12.sp) } } } } }
fun signed(value: Int) = if (value >= 0) "+$value" else value.toString()

@Composable fun BattlePage(dragon: Dragon, build: Build, onWin: () -> Unit, onLose: () -> Unit) {
    var playerHp by remember(dragon, build) { mutableStateOf((dragon.hp + build.hp).coerceAtLeast(1)) }
    var enemyHp by remember(dragon, build) { mutableStateOf(300) }
    var message by remember(dragon, build) { mutableStateOf("敌方训练机甲出现！") }
    var finished by remember(dragon, build) { mutableStateOf(false) }
    var rewardClaimed by remember(dragon, build) { mutableStateOf(false) }
    val maxPlayerHp = (dragon.hp + build.hp).coerceAtLeast(1)
    val playerAttack = (dragon.attack + build.attack).coerceAtLeast(1)
    val defense = (dragon.defense + build.defense).coerceAtLeast(1)
    Column(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("训练战", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        BattleCard("敌方训练机甲", "🤖", enemyHp, 300, Color(0xFFFEE2E2))
        BattleCard("${dragon.name} · ${build.body.name}", dragon.emoji + "🤖", playerHp, maxPlayerHp, Color(0xFFE0F2FE))
        Card(shape = RoundedCornerShape(18.dp)) { Column(Modifier.padding(18.dp)) { Text(message, fontWeight = FontWeight.Bold); Spacer(Modifier.height(8.dp)); Text("你的攻击：$playerAttack · 防御：$defense", color = Color.Gray, fontSize = 13.sp); if (finished && enemyHp <= 0) Text("奖励：+30 金币", color = Color(0xFF15803D), fontWeight = FontWeight.Bold) } }
        Button(onClick = { if (finished) { playerHp = maxPlayerHp; enemyHp = 300; message = "新的训练战开始！"; finished = false; rewardClaimed = false } else { val damage = (playerAttack + (0..20).random()).coerceAtLeast(1); enemyHp = (enemyHp - damage).coerceAtLeast(0); if (enemyHp <= 0) { message = "胜利！训练机甲被击破。"; finished = true; if (!rewardClaimed) { rewardClaimed = true; onWin() } } else { val enemyDamage = (40 - defense / 8).coerceIn(8, 40); playerHp = (playerHp - enemyDamage).coerceAtLeast(0); if (playerHp <= 0) { message = "战败！重新调整机甲组合吧。"; finished = true; if (!rewardClaimed) { rewardClaimed = true; onLose() } } else message = "你造成 $damage 点伤害，敌方反击 $enemyDamage 点。" } } }, modifier = Modifier.fillMaxWidth().height(55.dp)) { Text(if (finished) "再战一次" else "发动攻击") }
    }
}

@Composable fun BattleCard(title: String, icon: String, hp: Int, maxHp: Int, background: Color) { Card(colors = CardDefaults.cardColors(containerColor = background), shape = RoundedCornerShape(20.dp)) { Column(Modifier.fillMaxWidth().padding(18.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Text(icon, fontSize = 38.sp); Spacer(Modifier.width(12.dp)); Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp) }; Spacer(Modifier.height(10.dp)); Text("HP $hp / $maxHp") } } }

@Composable fun PartsPage(parts: List<Part>) { LazyColumn(Modifier.fillMaxSize().padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) { item { Text("机甲仓库", fontSize = 28.sp, fontWeight = FontWeight.Bold); Text("V1.1 已扩充至 ${parts.size} 个基础部件", color = Color.Gray) }; items(parts) { part -> Card(shape = RoundedCornerShape(14.dp)) { Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) { Text(part.emoji, fontSize = 25.sp); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(part.name, fontWeight = FontWeight.Bold); Text("${part.slot} · 攻击 ${signed(part.attack)} · 防御 ${signed(part.defense)} · HP ${signed(part.hp)} · 速度 ${signed(part.speed)}", color = Color.Gray, fontSize = 11.sp) } } } } } }
