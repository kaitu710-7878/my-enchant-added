# Changelog / 変更履歴

## [1.20.1-1.0.0-alpha.5] - 2026-11-19

### English
- Replaced per-tick locked-slot snapshot polling with zero-overhead, event-driven `NonNullList#set` interception (`NonNullListMixin` bound to player `Inventory` lists), preventing direct list tampering in-place without tick overhead or equipment-change side effects.

### 日本語
- 毎tickのスナップショット監視を廃止し、プレイヤーの `Inventory` リストに紐づけた `NonNullList#set` へのゼロオーバーヘッドな割り込み保護（`NonNullListMixin`）へ置き換えました。これにより、負荷や装備変更イベントの副作用なしにインベントリ配列への直接改変をその場で阻止します。

## [1.20.1-1.0.0-alpha.4] - 2026-11-19

### English
- Fixed a `StackOverflowError` in `RatioSharpnessEnchantment` and `AutomaticRepairEnchantment` caused by mutual recursion between `canEnchant` and `canApplyAtEnchantingTable` (notably with Apotheosis).
- Added server-side locked-slot snapshot enforcement in `PlayerTickEvent`, `ItemEntityMixin` discard protection, and `PlayerInteractEvent.RightClickItem` cancellation for locked armor to prevent forced unequipping, drops, and right-click equipping.

### 日本語
- `RatioSharpnessEnchantment` および `AutomaticRepairEnchantment` において `canEnchant` と `canApplyAtEnchantingTable` が相互再帰して `StackOverflowError` でクラッシュする不具合（Apotheosis 等との併用時）を修正。
- サーバー側 `PlayerTickEvent` によるロックスロットのスナップショット復元、`ItemEntityMixin` によるドロップアイテム破棄、およびロック済み防具の右クリック装備 (`PlayerInteractEvent.RightClickItem`) キャンセルを追加し、他MODによる直接的な防具剥奪や強制ドロップへの耐性を強化。

## [1.20.1-1.0.0-alpha.3] - 2026-11-19

### English
- Fixed **Rock** lock/unlock toggling across all container screens (including Creative inventory) by using the underlying inventory slot index (`Slot#getSlotIndex()`).
- Strengthened **Rock** forced-drop protection generically via `Player#setItemSlot`, `Inventory#removeItem`, `Inventory#removeItemNoUpdate`, `Inventory#setItem`, and `EntityJoinLevelEvent` (`ItemEntity` spawn cancellation).
- Added clarification about **Ratio Sharpness** scaling only the weapon modifier (excluding unarmed base damage of `1`) in `README.md` and language files.

### 日本語
- **Rock (ロック)** のロック/解除切り替えがクリエイティブ画面等を含む全インベントリ画面で確実に機能するよう、インベントリ実スロットインデックス (`Slot#getSlotIndex()`) を使用するよう修正。
- **Rock (ロック)** の強制ドロップ無力化を汎用的に強化（`Player#setItemSlot`、`Inventory#removeItem` / `removeItemNoUpdate` / `setItem` の保護、および `EntityJoinLevelEvent` での `ItemEntity` スポーン抑止を追加）。
- **Ratio Sharpness (割合鋭利)** が素手の攻撃力(1)を除いた武器補正のみを増加させる仕様について、`README.md` および言語ファイルに補足を追記。

## [1.20.1-1.0.0-alpha.2] - 2026-11-19

### English
- Changed **Ratio Sharpness** to scale the weapon's own `ADDITION` attack damage attribute modifiers by `+4% * level` (instead of applying `MULTIPLY_BASE` to the player's base attack damage).

### 日本語
- **Ratio Sharpness (割合鋭利)** の挙動を変更し、プレイヤーの基礎攻撃力への `MULTIPLY_BASE` ではなく、武器自身が持つ攻撃力Attribute (`ADDITION`) を `+4% × レベル` 増加させるように修正。

## [1.20.1-1.0.0-alpha.1] - 2026-11-19

### English
- Initial release for Minecraft 1.20.1 (Forge).
- Added 7 enchantments with no incompatibilities: Simple Protection, Armor, Hardness, Haste, Ratio Sharpness, Rock, and Automatic Repair.
- Added English (`en_us`) and Japanese (`ja_jp`) localizations.

### 日本語
- Minecraft 1.20.1 (Forge) 向け初期リリース。
- 競合なしの7種類の新規エンチャント（Simple Protection / Armor / Hardness / Haste / Ratio Sharpness / Rock / Automatic Repair）を追加。
- 英語 (`en_us`) および日本語 (`ja_jp`) の言語ファイルを追加。
