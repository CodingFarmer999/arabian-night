-- ==========================================
-- 1. 基礎字典 (Dictionaries)
-- ==========================================

-- 形容詞
CREATE TABLE adjectives (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- 玩家行動
CREATE TABLE actions (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

-- ==========================================
-- 2. 遭遇生成系統 (Encounter Lookups)
-- ==========================================

-- 遭遇查表 (統一儲存 Map Encounters 與 Adjective Lookups)
CREATE TABLE encounter_lookups (

    -- 對應書本標題數字： 1-121
    encounter_id INTEGER NOT NULL,
    
    -- 骰數：1-12
    die_roll INTEGER NOT NULL,
    
    -- 完整名稱 (快取顯示用，如 "偽裝的乞丐")
    full_text VARCHAR(100),
    
    -- 查表結果
    adj_id INTEGER, 
    
    -- 反應代碼 (A-O)
    reaction_letter CHAR(1), 
    
    -- 設定自然主鍵 (Natural Key)
    -- 透過 (表編號 + 骰數) 就能唯一鎖定一筆資料
    PRIMARY KEY (encounter_id, die_roll)
);

-- ==========================================
-- 3. 反應系統 (Reaction System)
-- ==========================================

-- 反應矩陣總表 (3D 座標：矩陣 + 形容詞 + 動作 -> 結果)
CREATE TABLE reaction_matrices (
    -- 矩陣代號 (L, O...)
    matrix_letter CHAR(1) NOT NULL,
    
    -- 邏輯關聯 ID (移除 references)
    adj_id INTEGER NOT NULL,
    action_id INTEGER NOT NULL,
    
    -- 結果指向 Story Event ID
    target_event_id INTEGER NOT NULL,
    
    -- 複合主鍵仍須保留，以確保資料唯一性
    PRIMARY KEY (matrix_letter, adj_id, action_id)
);

-- ==========================================
-- 4. 故事內容系統 (Story Content)
-- ==========================================

-- 事件主表
CREATE TABLE events (
    id INTEGER PRIMARY KEY,        -- 手動輸入書本編號 (174, 209...)
    type VARCHAR(10) NOT NULL,     -- 'LOCATION' 或 'STORY'
    title VARCHAR(100),
    description TEXT               -- 段落文字 
);

-- ==========================================
-- 5. 特殊地點系統 (Locations)
-- ==========================================

-- 地點選項
CREATE TABLE location_options (
    id SERIAL PRIMARY KEY,
    event_id INTEGER NOT NULL,     -- 邏輯指向 events.id
    option_index INTEGER NOT NULL, -- 1, 2, 3
    option_text TEXT NOT NULL
);

-- 地點擲骰結果
CREATE TABLE location_results (
    id SERIAL PRIMARY KEY,
    event_id INTEGER NOT NULL,      -- 來源 event
    option_index INTEGER NOT NULL,
    die_roll VARCHAR(10) NOT NULL,  -- '-', 'BLANK', '+'
    goto_event_id INTEGER NOT NULL  -- 目標 event
);

-- ==========================================
-- 6. 故事判定系統 (Story Outcomes)
-- ==========================================

-- 故事段落詳情
CREATE TABLE story_outcomes (
    id SERIAL PRIMARY KEY,
    event_id INTEGER NOT NULL,      -- 邏輯指向 events.id
    
    -- 巢狀選擇 (指向自己的 id)
    parent_id INTEGER DEFAULT NULL,
    
    -- 判定條件
    condition_type VARCHAR(20),     -- 'DEFAULT', 'SKILL'...
    condition_param VARCHAR(50),
    
    -- 結果與獎勵
    outcome_text TEXT,
    reward_str VARCHAR(100)
);