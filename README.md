# My Enchant Added / マイ・エンチャント・アデッド

- [English](#english)
- [日本語](#japanese)

---

<a id="english"></a>
## English

My Enchant Added is a Minecraft 1.20.1 (Forge) mod adding 7 enchantments designed for compatibility and stability.

### Enchantments
- **Simple Protection** (Max Lv. 4, Armor): Reduces final incoming damage by `0.01 * level` after all other mitigations. Stacks across equipped armor.
- **Armor** (Max Lv. 5, Armor): Adds `+0.4 * level` Armor attribute when equipped.
- **Hardness** (Max Lv. 5, Armor): Adds `+0.3 * level` Armor Toughness attribute when equipped.
- **Haste** (Max Lv. 2, Boots): Adds `+7.5 * level %` Movement Speed attribute (`MULTIPLY_BASE`).
- **Ratio Sharpness** (Max Lv. 5, Swords & Axes): Increases the weapon's attack damage attribute (`ADDITION`) by `4 * level %`. Note: Tooltip attack damage equals `unarmed base damage (1) + weapon modifier`. Ratio Sharpness scales only the `weapon modifier` (`new modifier = original modifier * (1 + 0.04 * level)`), so the unarmed base `1` is not scaled and the displayed total may appear slightly lower than `displayed * (1 + ratio)`.
- **Rock** (Max Lv. 1, All enchantable items): Right-click in inventory to lock/unlock. Prevents dropping, slot movement, and forced drops. Status is shown in the tooltip.
- **Automatic Repair** (Max Lv. 1, All damageable items): Repairs `0.01%` of maximum durability every tick.

### Installation
1. Install Minecraft 1.20.1 and Forge 47.3.0+.
2. Put `my_enchant_added-1.20.1-1.0.0-alpha.6.jar` into your `mods` folder.
3. Launch Minecraft.

---

<a id="japanese"></a>
## 日本語

My Enchant Added は、他MODとの互換性と安定した挙動を意識して作られた、7種類の新規エンチャントを追加する Minecraft 1.20.1 (Forge) 用MODです。

### 追加エンチャント
- **Simple Protection (シンプルプロテクション)** (最大Lv.4 / 防具): 他のすべての軽減計算後に受けるダメージを固定値で `0.01 × レベル` 軽減します（複数防具で合算）。
- **Armor (アーマー)** (最大Lv.5 / 防具): 装備時、防御力Attributeを `+0.4 × レベル` 増加させます。
- **Hardness (ハードネス)** (最大Lv.5 / 防具): 装備時、防具強度Attributeを `+0.3 × レベル` 増加させます。
- **Haste (ヘイスト)** (最大Lv.2 / ブーツ): 装備時、移動速度Attributeを `+7.5 × レベル %` 増加させます。
- **Ratio Sharpness (割合鋭利)** (最大Lv.5 / 剣・斧): 武器の攻撃力Attribute（`ADDITION`）を `+4 × レベル %` 増加させます。※補足：ツールチップの表示攻撃力は「素手の攻撃力(1)＋武器補正」で構成されており、割合鋭利は「武器補正」のみを `割合鋭利前の武器補正 × (1 + 0.04 × レベル)` に増加させます（素手の攻撃力分1は増えないため、表示値全体をそのまま掛けた値とは見た目が少し異なります。例：ダイヤ剣は表示7＝素手1＋武器6のため、Lv5で武器6×1.2＝7.2、表示8.2になります）。
- **Rock (ロック)** (最大Lv.1 / エンチャント可能な全アイテム): インベントリ上で右クリックしてロック/解除できます。ロック中はドロップ・スロット移動・強制ドロップが無効化され、ツールチップで状態を確認できます。
- **Automatic Repair (自動修復)** (最大Lv.1 / 耐久値を持つ全アイテム): 毎tick最大耐久値の `0.01%` ずつ回復します。

### 導入方法
1. Minecraft 1.20.1 および Forge 47.3.0 以降を導入します。
2. `my_enchant_added-1.20.1-1.0.0-alpha.6.jar` を `mods` フォルダに配置します。
3. Minecraft を起動します。
