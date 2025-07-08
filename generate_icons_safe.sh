#!/bin/bash

# 安全的 Android 图标生成脚本
# 正确处理自适应图标和 WebP 格式

echo "=== 安全的 Android 图标生成脚本 ==="

# 检查输入文件
if [ ! -f "icon.png" ]; then
    echo "❌ 错误：找不到 icon.png 文件"
    echo "请将您的图标文件重命名为 icon.png 并放在项目根目录下"
    exit 1
fi

# 创建备份目录
echo "📁 创建备份目录..."
mkdir -p icon_backup
cp -r app/src/main/res/mipmap-* icon_backup/ 2>/dev/null || true
cp -r app/src/main/res/drawable/ic_launcher_* icon_backup/ 2>/dev/null || true
echo "✅ 现有图标已备份到 icon_backup 目录"

# 创建临时目录
mkdir -p temp_icons

echo "🔄 开始生成 Android 图标..."

# 生成不同尺寸的图标（WebP 格式）
echo "生成 mdpi (48x48) 图标..."
sips -z 48 48 icon.png --out temp_icons/ic_launcher_mdpi.png
sips -s format webp temp_icons/ic_launcher_mdpi.png --out temp_icons/ic_launcher_mdpi.webp

echo "生成 hdpi (72x72) 图标..."
sips -z 72 72 icon.png --out temp_icons/ic_launcher_hdpi.png
sips -s format webp temp_icons/ic_launcher_hdpi.png --out temp_icons/ic_launcher_hdpi.webp

echo "生成 xhdpi (96x96) 图标..."
sips -z 96 96 icon.png --out temp_icons/ic_launcher_xhdpi.png
sips -s format webp temp_icons/ic_launcher_xhdpi.png --out temp_icons/ic_launcher_xhdpi.webp

echo "生成 xxhdpi (144x144) 图标..."
sips -z 144 144 icon.png --out temp_icons/ic_launcher_xxhdpi.png
sips -s format webp temp_icons/ic_launcher_xxhdpi.png --out temp_icons/ic_launcher_xxhdpi.webp

echo "生成 xxxhdpi (192x192) 图标..."
sips -z 192 192 icon.png --out temp_icons/ic_launcher_xxxhdpi.png
sips -s format webp temp_icons/ic_launcher_xxxhdpi.png --out temp_icons/ic_launcher_xxxhdpi.webp

# 复制 WebP 图标到对应的 mipmap 目录
echo "📋 复制图标到 mipmap 目录..."

# mdpi
cp temp_icons/ic_launcher_mdpi.webp app/src/main/res/mipmap-mdpi/ic_launcher.webp
cp temp_icons/ic_launcher_mdpi.webp app/src/main/res/mipmap-mdpi/ic_launcher_round.webp

# hdpi
cp temp_icons/ic_launcher_hdpi.webp app/src/main/res/mipmap-hdpi/ic_launcher.webp
cp temp_icons/ic_launcher_hdpi.webp app/src/main/res/mipmap-hdpi/ic_launcher_round.webp

# xhdpi
cp temp_icons/ic_launcher_xhdpi.webp app/src/main/res/mipmap-xhdpi/ic_launcher.webp
cp temp_icons/ic_launcher_xhdpi.webp app/src/main/res/mipmap-xhdpi/ic_launcher_round.webp

# xxhdpi
cp temp_icons/ic_launcher_xxhdpi.webp app/src/main/res/mipmap-xxhdpi/ic_launcher.webp
cp temp_icons/ic_launcher_xxhdpi.webp app/src/main/res/mipmap-xxhdpi/ic_launcher_round.webp

# xxxhdpi
cp temp_icons/ic_launcher_xxxhdpi.webp app/src/main/res/mipmap-xxxhdpi/ic_launcher.webp
cp temp_icons/ic_launcher_xxxhdpi.webp app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.webp

# 清理临时文件
rm -rf temp_icons

echo "✅ 图标生成完成！"
echo ""
echo "📊 生成的图标尺寸："
echo "- mdpi: 48x48 px (WebP)"
echo "- hdpi: 72x72 px (WebP)"
echo "- xhdpi: 96x96 px (WebP)"
echo "- xxhdpi: 144x144 px (WebP)"
echo "- xxxhdpi: 192x192 px (WebP)"
echo ""
echo "💾 原始图标已备份到 icon_backup 目录"
echo "🔄 如需恢复，请运行: cp -r icon_backup/* app/src/main/res/"
echo ""
echo "🔧 现在可以运行: ./gradlew assembleDebug 来测试构建" 