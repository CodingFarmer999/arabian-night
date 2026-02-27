# arabian-night (一千零一夜桌遊輔助工具) - Vibe Coding

這是一個為桌遊**《一千零一夜 (Tales of the Arabian Nights)》**所開發的專屬輔助系統。旨在幫助玩家在遊玩過程中，更方便地處理繁雜的劇本查閱、狀態紀錄與事件結算等機制，大幅提升整體的遊戲體驗。

## 🛠️ 技術棧 (Tech Stack)

- **後端架構**: Java 17, Spring Boot 3, Spring Web MVC
- **前端渲染與互動**: Thymeleaf, HTMX (提供現代化且無刷新的操作體驗)
- **資料儲存**: SQLite (`tales.db`), MyBatis
- **其他**: Lombok

## ✨ 核心功能 (Key Features)

- **遭遇生成與查表系統 (Encounter Lookups)**
  - 根據書本標題數字與骰數，自動查表產生詳細遭遇與反應代碼。
- **反應矩陣系統 (Reaction System)**
  - 結合玩家動作 (Action)、遭遇形容詞 (Adjective) 與對應的查表矩陣，自動計算出目標的故事事件編號。
- **故事內容展示 (Story Content)**
  - 整合遭遇描述與故事文本。
- **特殊地點與選擇 (Locations & Options)**
  - 支援特殊地點 (如洞穴、島嶼) 的多重選項，以及對應的擲骰結果判定。
- **故事判定與結果 (Story Outcomes)**
  - 處理故事內的巢狀選擇條件 (技能檢定等)。
  - 根據玩家的技能條件，提供不同的結果與獎勵描述。

## 🚀 啟動說明 (Getting Started)
*** 注意 ***
由於版權問題，本專案不提供 tales.db 檔案，請自行準備 tales.db 檔案。

*** 啟動步驟 ***
1. 請確認開發環境已安裝 Java 17 與 Maven。
2. 複製此專案至本地環境。
3. 進入 `tales` 資料夾：
   ```bash
   cd tales
   ```
4. 執行 Maven 指令以啟動服務 (並確認 `tales.db` 已存在於目錄下)：
   ```bash
   ./mvnw spring-boot:run
   ```
5. 開啟瀏覽器並前往 `http://localhost:8080` 開始使用。
