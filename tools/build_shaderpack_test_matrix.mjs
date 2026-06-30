import fs from "node:fs/promises";
import path from "node:path";
import { SpreadsheetFile, Workbook } from "@oai/artifact-tool";

const root = process.cwd();
const dataPath = "C:/tmp/gtp_modrinth_shaders_top20.json";
const outputDir = path.join(root, "outputs", "gtp_shader_matrix");
const outputPath = path.join(outputDir, "gtp_shaderpack_test_matrix_ja_2026-06-18.xlsx");
const sourceUrl = "https://api.modrinth.com/v2/search?facets=[[\"project_type:shader\"]]&index=downloads&limit=20";
const modrinthSearchUrl = "https://modrinth.com/shaders?o=downloads";
const versions = ["1.21.11", "26.1", "26.1.1", "26.1.2"];
const statusOptions = ["未検証", "正常", "軽微な問題", "不具合", "クラッシュ", "対象外"];
const resultOptions = [
  "未確認",
  "フェード正常",
  "フェードなし",
  "一部フェード",
  "深度/残像",
  "空/天体の問題",
  "クラッシュ",
  "その他",
];

const shaders = JSON.parse(await fs.readFile(dataPath, "utf8")).map((shader, index) => ({
  rank: index + 1,
  title: shader.title,
  slug: shader.slug,
  url: `https://modrinth.com/shader/${shader.slug}`,
  author: shader.author,
  downloads: shader.downloads,
  follows: shader.follows,
  categories: Array.isArray(shader.categories) ? shader.categories.join(", ") : "",
  description: shader.description ?? "",
}));

const workbook = Workbook.create();

function quoteSheet(name) {
  return `'${name.replace(/'/g, "''")}'`;
}

function sanitizeTableName(name) {
  return `T_${name.replace(/[^A-Za-z0-9_]/g, "_")}`;
}

function applyBaseSheetStyle(sheet) {
  sheet.showGridLines = false;
  sheet.freezePanes.freezeRows(3);
  setWidths(sheet, [48, 260, 190, 280, 130, 95, 110, 150, 145, 130, 130, 115, 240, 280]);
}

function setWidths(sheet, widths) {
  widths.forEach((width, index) => {
    const col = columnName(index + 1);
    sheet.getRange(`${col}1:${col}80`).format.columnWidthPx = width;
  });
}

function columnName(index) {
  let n = index;
  let name = "";
  while (n > 0) {
    const rem = (n - 1) % 26;
    name = String.fromCharCode(65 + rem) + name;
    n = Math.floor((n - 1) / 26);
  }
  return name;
}

function styleHeader(range) {
  range.format = {
    fill: "#111827",
    font: { bold: true, color: "#FFFFFF" },
    wrapText: true,
    horizontalAlignment: "center",
    verticalAlignment: "middle",
  };
}

function styleTitle(range) {
  range.format = {
    fill: "#0F172A",
    font: { bold: true, color: "#FFFFFF", size: 16 },
    horizontalAlignment: "left",
    verticalAlignment: "middle",
  };
}

function addStatusValidation(sheet, rangeAddress) {
  sheet.getRange(rangeAddress).dataValidation = {
    rule: { type: "list", values: statusOptions },
  };
}

function addResultValidation(sheet, rangeAddress) {
  sheet.getRange(rangeAddress).dataValidation = {
    rule: { type: "list", values: resultOptions },
  };
}

function addVersionSheet(version) {
  const sheet = workbook.worksheets.add(version);
  applyBaseSheetStyle(sheet);
  sheet.getRange("A1:N1").merge();
  sheet.getRange("A1").values = [[`GTP シェーダーパック検証表 - Minecraft ${version}`]];
  styleTitle(sheet.getRange("A1:N1"));
  sheet.getRange("A2:N2").merge();
  sheet.getRange("A2").values = [[
    "検証しながら「状態」と「結果」を入力します。挙動が分かりにくい場合は、メモを短く残してスクショ/動画リンクを入れてください。",
  ]];
  sheet.getRange("A2:N2").format = {
    fill: "#E5EEF8",
    font: { italic: true, color: "#1F2937" },
    wrapText: true,
  };

  const headers = [
    "順位",
    "シェーダー名",
    "スラッグ",
    "Modrinth URL",
    "作者",
    "ダウンロード数",
    "状態",
    "結果",
    "シェーダーバージョン",
    "GTPビルド",
    "プロファイル",
    "検証日",
    "メモ",
    "スクショ/動画リンク",
  ];
  sheet.getRange("A3:N3").values = [headers];
  styleHeader(sheet.getRange("A3:N3"));

  const rows = shaders.map((shader) => [
    shader.rank,
    shader.title,
    shader.slug,
    shader.url,
    shader.author,
    shader.downloads,
    "未検証",
    "未確認",
    "",
    "build31",
    version,
    "",
    "",
    "",
  ]);
  sheet.getRange(`A4:N${3 + rows.length}`).values = rows;
  sheet.getRange(`F4:F${3 + rows.length}`).format.numberFormat = "#,##0";
  sheet.getRange(`L4:L${3 + rows.length}`).format.numberFormat = "yyyy-mm-dd";
  sheet.getRange(`A3:N${3 + rows.length}`).format.borders = { preset: "all", style: "thin", color: "#CBD5E1" };
  sheet.getRange(`A4:N${3 + rows.length}`).format = {
    wrapText: true,
    verticalAlignment: "top",
  };
  addStatusValidation(sheet, `G4:G${3 + rows.length}`);
  addResultValidation(sheet, `H4:H${3 + rows.length}`);
  const table = sheet.tables.add(`A3:N${3 + rows.length}`, true, sanitizeTableName(version));
  table.style = "TableStyleMedium2";
  table.showFilterButton = true;
  return sheet;
}

function addSummarySheet() {
  const sheet = workbook.worksheets.getItem("概要");
  sheet.showGridLines = false;
  sheet.freezePanes.freezeRows(3);
  setWidths(sheet, [48, 260, 190, 120, 120, 120, 120, 260, 105, 110, 110]);

  sheet.getRange("A1:K1").merge();
  sheet.getRange("A1").values = [["GTP シェーダーパック互換性トラッカー"]];
  styleTitle(sheet.getRange("A1:K1"));
  sheet.getRange("A2:K2").merge();
  sheet.getRange("A2").values = [[
    "一覧はModrinthのShader Pack検索をダウンロード数順で取得したものです。各バージョンシートの状態を更新すると、この概要にも反映されます。",
  ]];
  sheet.getRange("A2:K2").format = {
    fill: "#E5EEF8",
    font: { italic: true, color: "#1F2937" },
    wrapText: true,
  };

  const headers = [
    "順位",
    "シェーダー名",
    "スラッグ",
    "1.21.11",
    "26.1",
    "26.1.1",
    "26.1.2",
    "主な問題/メモ",
    "優先度",
    "Modrinth",
    "ダウンロード数",
  ];
  sheet.getRange("A3:K3").values = [headers];
  styleHeader(sheet.getRange("A3:K3"));

  const rows = shaders.map((shader, index) => [
    shader.rank,
    shader.title,
    shader.slug,
    `=${quoteSheet("1.21.11")}!G${4 + index}`,
    `=${quoteSheet("26.1")}!G${4 + index}`,
    `=${quoteSheet("26.1.1")}!G${4 + index}`,
    `=${quoteSheet("26.1.2")}!G${4 + index}`,
    "",
    "",
    shader.url,
    shader.downloads,
  ]);
  sheet.getRange(`A4:K${3 + rows.length}`).values = rows.map((row) => row.map((value, col) => col >= 3 && col <= 6 ? null : value));
  sheet.getRange(`D4:G${3 + rows.length}`).formulas = rows.map((row) => row.slice(3, 7));
  sheet.getRange(`K4:K${3 + rows.length}`).format.numberFormat = "#,##0";
  sheet.getRange(`A3:K${3 + rows.length}`).format.borders = { preset: "all", style: "thin", color: "#CBD5E1" };
  sheet.getRange(`A4:K${3 + rows.length}`).format = { wrapText: true, verticalAlignment: "top" };

  const summaryRow = 27;
  sheet.getRange(`A${summaryRow}:K${summaryRow}`).merge();
  sheet.getRange(`A${summaryRow}`).values = [["進捗"]];
  sheet.getRange(`A${summaryRow}:K${summaryRow}`).format = {
    fill: "#1F2937",
    font: { bold: true, color: "#FFFFFF" },
  };
  sheet.getRange(`A${summaryRow + 1}:F${summaryRow + 1}`).values = [["バージョン", "検証済み", "正常", "軽微", "不具合/クラッシュ", "未検証"]];
  styleHeader(sheet.getRange(`A${summaryRow + 1}:F${summaryRow + 1}`));
  const progressRows = versions.map((version) => [
    version,
    `=20-COUNTIF(${quoteSheet(version)}!$G$4:$G$23,"未検証")`,
    `=COUNTIF(${quoteSheet(version)}!$G$4:$G$23,"正常")`,
    `=COUNTIF(${quoteSheet(version)}!$G$4:$G$23,"軽微な問題")`,
    `=COUNTIF(${quoteSheet(version)}!$G$4:$G$23,"不具合")+COUNTIF(${quoteSheet(version)}!$G$4:$G$23,"クラッシュ")`,
    `=COUNTIF(${quoteSheet(version)}!$G$4:$G$23,"未検証")`,
  ]);
  sheet.getRange(`A${summaryRow + 2}:A${summaryRow + 5}`).values = progressRows.map((row) => [row[0]]);
  sheet.getRange(`B${summaryRow + 2}:F${summaryRow + 5}`).formulas = progressRows.map((row) => row.slice(1));
  sheet.getRange(`A${summaryRow + 1}:F${summaryRow + 5}`).format.borders = { preset: "all", style: "thin", color: "#CBD5E1" };

  sheet.getRange("H28:K31").values = [
    ["おすすめの検証手順", null, null, null],
    ["1. まずシェーダー個別対応なしで試す。", null, null, null],
    ["2. シェーダーの正確なバージョンと見えた症状を残す。", null, null, null],
    ["3. 26.1.2で問題が出る人気パックを優先する。", null, null, null],
  ];
  sheet.getRange("H28:K31").format = {
    fill: "#F8FAFC",
    wrapText: true,
  };
  sheet.getRange("H28:K31").format.borders = { preset: "outside", style: "thin", color: "#CBD5E1" };

  sheet.getRange("A34:K37").values = [
    ["参照元", modrinthSearchUrl, null, null, null, null, null, null, null, null, null],
    ["API取得元", sourceUrl, null, null, null, null, null, null, null, null, null],
    ["取得日", "2026-06-18", null, null, null, null, null, null, null, null, null],
    ["補足", "状態は編集できます。シェーダーパックの人気順は時間とともに変わる可能性があります。", null, null, null, null, null, null, null, null, null],
  ];
  sheet.getRange("A34:K37").format = {
    fill: "#F8FAFC",
    wrapText: true,
  };
  sheet.getRange("A34:K37").format.borders = { preset: "all", style: "thin", color: "#CBD5E1" };

  const table = sheet.tables.add(`A3:K${3 + shaders.length}`, true, "T_Summary");
  table.style = "TableStyleMedium4";
  table.showFilterButton = true;
  return sheet;
}

workbook.worksheets.add("概要");
for (const version of versions) {
  addVersionSheet(version);
}
addSummarySheet();

await fs.mkdir(outputDir, { recursive: true });

for (const sheetName of ["概要", ...versions]) {
  const preview = await workbook.render({ sheetName, autoCrop: "all", scale: 1, format: "png" });
  await fs.writeFile(path.join(outputDir, `${sheetName.replace(/\./g, "_")}_preview.png`), new Uint8Array(await preview.arrayBuffer()));
}

const errorScan = await workbook.inspect({
  kind: "match",
  searchTerm: "#REF!|#DIV/0!|#VALUE!|#NAME\\?|#N/A",
  options: { useRegex: true, maxResults: 50 },
  summary: "formula error scan",
});
console.log(errorScan.ndjson);

const xlsx = await SpreadsheetFile.exportXlsx(workbook);
await xlsx.save(outputPath);
console.log(outputPath);
