@echo off

if ".%1." NEQ ".." goto build
echo "usage: ic_launcher.cmd <size>"
goto:eof

:build

magick -size %1x%1 xc:none -fill White -draw "rectangle 0,0 %1,%1" tmp_white.png
magick -size %1x%1 xc:none -fill Black -draw "rectangle 0,0 %1,%1" tmp_black.png
magick shield.svg -resize %1x%1 tmp_shield.png
magick clip.svg -resize %1x%1 tmp_clip.png
magick mask-glow.svg -resize %1x%1 tmp_mask-glow.png
magick mask-dark.svg -resize %1x%1 tmp_mask-dark.png
magick shadow.svg -resize %1x%1 tmp_shadow.png
magick tmp_shield.png tmp_clip.png -alpha off -compose CopyOpacity -composite -resize %1x%1 tmp_base.png
magick tmp_white.png tmp_mask-glow.png -alpha off -compose CopyOpacity -composite -resize %1x%1 tmp_glow.png
magick tmp_black.png tmp_mask-dark.png -alpha off -compose CopyOpacity -composite -resize %1x%1 tmp_dark.png
magick tmp_black.png tmp_shadow.png -alpha off -compose CopyOpacity -composite -resize %1x%1 tmp_black-shadow.png
magick tmp_black-shadow.png tmp_base.png -composite tmp_shadowed.png
magick tmp_shadowed.png tmp_glow.png -composite tmp_glowing.png
magick tmp_glowing.png tmp_dark.png -composite ic_launcher.png
del tmp_*
