// 发布 v1.1.13.1：建草稿 release → 上传 5 个 jar → PATCH 修回被上传端点改写的文件名 → 转正 → 核对。
// token 取自 git credential fill，绝不回显。
const fs = require('fs');
const path = require('path');
const { execSync } = require('child_process');

const REPO = 'Dobelring/KaleidoscopeChineseFood-Refabricated';
const TAG = 'v1.1.13.1';
const TARGET = '1.21.1-fabric';
const ROOT = 'D:/dsh_workSpace/Kaleidoscope_Chinese_Food_fabric';

const JARS = [
  ['1.21.1/Kaleidoscope-ChineseFood-1.21.1-Fabric/build/libs/kaleidoscope_chinesefood-1.1.13.1-fabric+mc1.21.1.jar', '1.21.1', '21'],
  ['1.20.1/Kaleidoscope-ChineseFood-1.20.1-Fabric/build/libs/kaleidoscope_chinesefood-1.1.13.1-fabric+mc1.20.1.jar', '1.20.1', '17'],
  ['26.1/Kaleidoscope-ChineseFood-26.1.2-Fabric-new/build/libs/kaleidoscope_chinesefood-1.1.13.1-fabric+mc26.1.2.jar', '26.1.2', '25'],
  ['26.2/Kaleidoscope-ChineseFood-26.2-Fabric-new/build/libs/kaleidoscope_chinesefood-1.1.13.1-fabric+mc26.2.jar', '26.2', '25'],
  ['26.3/Kaleidoscope-ChineseFood-26.3-Fabric/build/libs/kaleidoscope_chinesefood-1.1.13.1-fabric+mc26.3.jar', '26.3', '25']
];

const BODY = `## 森罗物语：国味 Fabric 移植版 v1.1.13.1

| 资产文件 | 适用版本 |
|---|---|
| \`kaleidoscope_chinesefood-1.1.13.1-fabric+mc26.3.jar\` | Minecraft 26.3 |
| \`kaleidoscope_chinesefood-1.1.13.1-fabric+mc26.2.jar\` | Minecraft 26.2 |
| \`kaleidoscope_chinesefood-1.1.13.1-fabric+mc26.1.2.jar\` | Minecraft 26.1.x |
| \`kaleidoscope_chinesefood-1.1.13.1-fabric+mc1.21.1.jar\` | Minecraft 1.21.1 |
| \`kaleidoscope_chinesefood-1.1.13.1-fabric+mc1.20.1.jar\` | Minecraft 1.20.1 |

## 本次更新

- 对齐官方 1.1.13-neoforge+1.21.1
- **新增中秋登录礼**：中秋节期间（各年中秋起 3 天）每天首次登录赠送一个月饼并授予限定进度；活动日期：2026-09-24、2027-09-15、2028-10-03、2029-09-22、2030-09-12、2031-10-01、2032-09-19
- **冰箱掉落整理**：爆炸等一次破坏冰箱两半的场景不再双倍掉落；破坏任意一半均会掉落冰箱本体（官方 1.1.13 拆上半不再掉落方块，移植版按原版双方块惯例保留双侧掉落）
- 1.20.1：生竹筒蒸蛋 / 生肠粉的配方分类修正（misc → food）
- 模组显示名改为 **Kaleidoscope ChineseFood Refabricated**（物品 / 方块信息下方的来源标注随之更新；模组 ID 不变）

## 前置模组（必装）

- **Fabric API**（按游戏版本选择对应分支）
- **Kaleidoscope Cookery**（Refabricated 版；26.x 建议 1.5.1.1 及以上）
- **Forge Config API Port**

## 说明

- 仅支持 Fabric；NeoForge 用户请使用原版《森罗物语：国味》
- 需要 Java 25 及以上（1.21.1 版为 Java 21，1.20.1 版为 Java 17）
- 1.20.1 版建议同时安装 **Create**（或 Porting Lib 全套）：cookery 1.20.1 Fabric 运行时依赖 Porting Lib 的 entity / loot 模块，该模块平时由 Create 的嵌套 jar 提供
- 本版本不含 1.21.11（该分支仍为 1.1.10，见 v1.1.10.3 release）；官方 1.1.13 中森罗机关装置（Create 附属）的联动补项未同步——该附属模组没有 Fabric 版`;

const cred = execSync('printf "protocol=https\\nhost=github.com\\n\\n" | git credential fill', { encoding: 'utf8' });
const token = cred.split('\n').find(l => l.startsWith('password=')).slice('password='.length).trim();

async function api(url, init = {}) {
  const res = await fetch(url.startsWith('http') ? url : 'https://api.github.com' + url, {
    ...init,
    headers: {
      Authorization: 'Bearer ' + token,
      Accept: 'application/vnd.github+json',
      'User-Agent': 'chinesefood-release-script',
      ...(init.headers || {})
    }
  });
  const text = await res.text();
  if (!res.ok) throw new Error(`${res.status} ${res.statusText} @ ${url}\n${text.slice(0, 400)}`);
  return text ? JSON.parse(text) : null;
}

(async () => {
  for (const [rel] of JARS) {
    if (!fs.existsSync(path.join(ROOT, rel))) throw new Error('jar 缺失: ' + rel);
  }

  const existing = await api(`/repos/${REPO}/releases/tags/${TAG}`).catch(() => null);
  if (existing) throw new Error(`tag ${TAG} 已存在 release（id ${existing.id}），请人工确认后再处理`);

  const release = await api(`/repos/${REPO}/releases`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ tag_name: TAG, target_commitish: TARGET, name: TAG, body: BODY, draft: true, prerelease: false })
  });
  console.log(`草稿 release 已建：id=${release.id} tag=${release.tag_name} target=${release.target_commitish}`);

  for (const [rel, mc, java] of JARS) {
    const full = path.join(ROOT, rel);
    const wanted = path.basename(full);
    const bytes = fs.readFileSync(full);
    const uploadUrl = release.upload_url.replace('{?name,label}', '?name=' + encodeURIComponent(wanted));
    let asset = await api(uploadUrl, {
      method: 'POST',
      headers: { 'Content-Type': 'application/java-archive' },
      body: bytes
    });
    if (asset.name !== wanted) {
      const fixed = await api(`/repos/${REPO}/releases/assets/${asset.id}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: wanted })
      });
      console.log(`  上传 ${wanted} → 被端点改名为 ${asset.name} → 已 PATCH 回 ${fixed.name}`);
      asset = fixed;
    } else {
      console.log(`  上传 ${wanted}（名称未被改写）`);
    }
    console.log(`    mc=${mc} java=${java} size=${asset.size} state=${asset.state}`);
  }

  const published = await api(`/repos/${REPO}/releases/${release.id}`, {
    method: 'PATCH',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ draft: false })
  });
  console.log(`\n已转正：${published.html_url}  draft=${published.draft}`);

  const check = await api(`/repos/${REPO}/releases/tags/${TAG}`);
  console.log('资产核对：');
  for (const a of check.assets) console.log(`  ${a.name}  (${a.size} bytes, ${a.state})`);
})().catch(e => { console.error('FAILED: ' + e.message); process.exit(1); });
