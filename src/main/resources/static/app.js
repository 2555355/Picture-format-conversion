/* =========================================================
   PIXEL · 优雅现代风 - 双方向应用逻辑 (横屏 + 竖屏)
   ========================================================= */
const ICON = {
  'convert':    '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><polyline points="17 1 21 5 17 9"/><path d="M3 11V9a4 4 0 0 1 4-4h14"/><polyline points="7 23 3 19 7 15"/><path d="M21 13v2a4 4 0 0 1-4 4H3"/></svg>',
  'resize':     '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M3 7V3h4M21 7V3h-4M3 17v4h4M21 17v4h-4"/><path d="M7 12h10"/></svg>',
  'scale':      '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="11" cy="11" r="7"/><line x1="21" y1="21" x2="16.65" y2="16.65"/></svg>',
  'rotate':     '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><polyline points="23 4 23 10 17 10"/><path d="M20.49 15A9 9 0 1 1 19 6.5L23 10"/></svg>',
  'flip':       '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><polyline points="17 1 21 5 17 9"/><path d="M3 11V9a4 4 0 0 1 4-4h14"/><polyline points="7 23 3 19 7 15"/><path d="M21 13v2a4 4 0 0 1-4 4H3"/></svg>',
  'grayscale':  '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 3v18M8 3v18M16 3v18M4 3v18M20 3v18"/></svg>',
  'invert':     '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 3a9 9 0 0 0 0 18z" fill="currentColor"/></svg>',
  'sepia':      '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18"/><circle cx="12" cy="12" r="4"/></svg>',
  'brightness': '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="5"/><line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/></svg>',
  'blur':       '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"/><circle cx="5" cy="6" r="2"/><circle cx="19" cy="6" r="2"/><circle cx="5" cy="18" r="2"/><circle cx="19" cy="18" r="2"/></svg>',
  'sharpen':    '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>',
  'pixelate':   '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/></svg>',
  'vignette':   '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><circle cx="12" cy="12" r="5" fill="currentColor" opacity="0.3"/></svg>',
  'border':     '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18"/><rect x="7" y="7" width="10" height="10" fill="currentColor" opacity="0.2"/></svg>',
  'round':      '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="18" height="18" rx="8"/></svg>',
  'watermark':  '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z"/></svg>',
  'ascii':      '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><polyline points="4 7 4 4 20 4 20 7"/><line x1="9" y1="20" x2="15" y2="20"/><line x1="12" y1="4" x2="12" y2="20"/></svg>',
  'base64':     '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><polyline points="16 18 22 12 16 6"/><polyline points="8 6 2 12 8 18"/></svg>',
  'crop':       '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M6 2v14a2 2 0 0 0 2 2h14"/><path d="M18 22V8a2 2 0 0 0-2-2H2"/></svg>',
  'info':       '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>',
  'posterize':  '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M12 2v10l5 5"/></svg>',
  'hsb':        '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="13.5" cy="6.5" r=".5" fill="currentColor"/><path d="M12 2C6.5 2 2 6.5 2 12s4.5 10 10 10c.926 0 1.648-.746 1.648-1.688 0-.437-.18-.835-.437-1.125-.29-.289-.438-.652-.438-1.125a1.64 1.64 0 0 1 1.668-1.668h1.996c3.051 0 5.555-2.503 5.555-5.554C21.965 6.012 17.461 2 12 2z"/></svg>',
  'threshold':  '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/><path d="M12 3a9 9 0 0 0 0 18" fill="currentColor"/></svg>',
  'colorbalance':'<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="M12 2v20M2 12h20"/></svg>',
  'channel':    '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="18"/><rect x="14" y="3" width="7" height="18"/></svg>',
  'circle':     '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="9"/></svg>'
};

/* ===== ACTIONS ===== */
const ACTIONS = [
  { key: 'convert',   group: 'main',     label: '格式转换', icon: 'convert', desc: '把图片转换成 PNG / JPG / GIF / BMP / TIFF', fields: [
    { name: 'format', label: '目标格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' },
    { name: 'quality', label: '质量 (JPEG)', type: 'range', min: 0.3, max: 1, step: 0.05, value: 0.9, show: v => v.toFixed(2) }
  ]},
  { key: 'resize',    group: 'main',     label: '调整尺寸', icon: 'resize', desc: '按指定宽高缩放', fields: [
    { name: 'width', label: '宽度 (px)', type: 'number', value: 800, min: 1, max: 10000 },
    { name: 'height', label: '高度 (px)', type: 'number', value: 600, min: 1, max: 10000 },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'scale',     group: 'main',     label: '等比缩放', icon: 'scale', desc: '按百分比缩放，保持宽高比', fields: [
    { name: 'percent', label: '缩放比例', type: 'range', min: 10, max: 200, step: 5, value: 50, show: v => v + '%' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'rotate',    group: 'main',     label: '旋转', icon: 'rotate', desc: '按任意角度旋转', fields: [
    { name: 'degrees', label: '旋转角度', type: 'range', min: -180, max: 180, step: 1, value: 90, show: v => v + '°' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'flip',      group: 'main',     label: '翻转', icon: 'flip', desc: '水平或垂直翻转', fields: [
    { name: 'direction', label: '翻转方向', type: 'select', options: ['horizontal','vertical'], value: 'horizontal' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'crop',      group: 'main',     label: '裁切', icon: 'crop', desc: '按坐标裁出矩形区域', fields: [
    { name: 'x', label: '起始 X', type: 'number', value: 0, min: 0 },
    { name: 'y', label: '起始 Y', type: 'number', value: 0, min: 0 },
    { name: 'width', label: '宽度', type: 'number', value: 200, min: 1 },
    { name: 'height', label: '高度', type: 'number', value: 200, min: 1 },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'grayscale',          group: 'filter', label: '黑白', icon: 'grayscale', desc: '变为灰度照片', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'invert',             group: 'filter', label: '反色', icon: 'invert', desc: '反转为底片效果', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'sepia',              group: 'filter', label: '复古', icon: 'sepia', desc: '怀旧棕色调', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'brightness-contrast',group: 'filter', label: '亮度对比度', icon: 'brightness', desc: '微调亮度和对比度', fields: [
    { name: 'brightness', label: '亮度', type: 'range', min: -1, max: 1, step: 0.05, value: 0.1, show: v => v.toFixed(2) },
    { name: 'contrast', label: '对比度', type: 'range', min: -1, max: 1, step: 0.05, value: 0.1, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'hsb',                group: 'filter', label: '色相饱和度', icon: 'hsb', desc: '调整色相、饱和度、亮度', fields: [
    { name: 'hue', label: '色相', type: 'range', min: -1, max: 1, step: 0.05, value: 0, show: v => v.toFixed(2) },
    { name: 'saturation', label: '饱和度', type: 'range', min: -1, max: 1, step: 0.05, value: 0.1, show: v => v.toFixed(2) },
    { name: 'brightness', label: '亮度', type: 'range', min: -1, max: 1, step: 0.05, value: 0, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'color_balance',      group: 'filter', label: '色彩平衡', icon: 'colorbalance', desc: '调整 RGB 三通道平衡', fields: [
    { name: 'red', label: '红色', type: 'range', min: -100, max: 100, step: 1, value: 0, show: v => v },
    { name: 'green', label: '绿色', type: 'range', min: -100, max: 100, step: 1, value: 0, show: v => v },
    { name: 'blue', label: '蓝色', type: 'range', min: -100, max: 100, step: 1, value: 0, show: v => v },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'posterize',          group: 'filter', label: '色调分离', icon: 'posterize', desc: '减少色阶做出色块化', fields: [
    { name: 'levels', label: '色阶数', type: 'range', min: 2, max: 16, step: 1, value: 4, show: v => v },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'threshold',          group: 'filter', label: '二值化', icon: 'threshold', desc: '按阈值转为黑白', fields: [
    { name: 'threshold', label: '阈值', type: 'range', min: 0, max: 255, step: 1, value: 128, show: v => v },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'cool', group: 'cinematic', label: '冷色调', icon: 'colorbalance', desc: '增强蓝青色', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'warm', group: 'cinematic', label: '暖色调', icon: 'colorbalance', desc: '增强红黄色', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'vintage', group: 'cinematic', label: '复古胶片', icon: 'sepia', desc: '老式胶片质感', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.6, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'cinematic', group: 'cinematic', label: '电影感', icon: 'colorbalance', desc: '高对比+色调偏移', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'bw-high-contrast', group: 'cinematic', label: '高对比黑白', icon: 'grayscale', desc: '强对比黑白', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'film-grain', group: 'cinematic', label: '胶片颗粒', icon: 'posterize', desc: '叠加胶片噪点', fields: [
    { name: 'amount', label: '颗粒强度', type: 'range', min: 0.05, max: 0.5, step: 0.05, value: 0.15, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'lomo', group: 'cinematic', label: 'LOMO', icon: 'vignette', desc: '暗角+过饱和', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'hdr-look', group: 'cinematic', label: 'HDR 模拟', icon: 'brightness', desc: '模拟 HDR 效果', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'cross-process', group: 'cinematic', label: '交叉冲印', icon: 'colorbalance', desc: '非常规色彩冲印', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'faded', group: 'cinematic', label: '褪色', icon: 'brightness', desc: '降低对比与饱和做褪色', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'vivid', group: 'cinematic', label: '鲜艳', icon: 'hsb', desc: '强化饱和度与对比', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'noir', group: 'cinematic', label: '黑色电影', icon: 'grayscale', desc: '高反差黑白', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'matte', group: 'cinematic', label: '哑光', icon: 'vignette', desc: '哑光低饱和高光', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'punch', group: 'cinematic', label: 'Punch', icon: 'sharpen', desc: '强化色彩与对比', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'fade-warm', group: 'cinematic', label: '暖色褪色', icon: 'sepia', desc: '暖调褪色', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'blur',   group: 'effect', label: '高斯模糊', icon: 'blur', desc: '高斯模糊滤镜', fields: [
    { name: 'radius', label: '模糊半径', type: 'range', min: 1, max: 50, step: 1, value: 5, show: v => v + 'px' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'sharpen',group: 'effect', label: '锐化', icon: 'sharpen', desc: 'USM 锐化滤镜', fields: [
    { name: 'amount', label: '锐化强度', type: 'range', min: 0, max: 3, step: 0.1, value: 1, show: v => v.toFixed(1) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'pixelate',group: 'effect', label: '像素化', icon: 'pixelate', desc: '马赛克效果', fields: [
    { name: 'size', label: '像素块大小', type: 'range', min: 2, max: 40, step: 1, value: 8, show: v => v + 'px' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'vignette',group: 'effect', label: '暗角', icon: 'vignette', desc: '四边压暗', fields: [
    { name: 'amount', label: '暗角强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'emboss', group: 'effect', label: '浮雕', icon: 'sharpen', desc: '浮雕效果', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 2, step: 0.1, value: 1, show: v => v.toFixed(1) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'edge-detect', group: 'effect', label: '边缘检测', icon: 'crop', desc: '边缘检测', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'oil-paint', group: 'effect', label: '油画', icon: 'sharpen', desc: '油画笔触', fields: [
    { name: 'radius', label: '笔触半径', type: 'range', min: 1, max: 8, step: 1, value: 4, show: v => v + 'px' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'mosaic', group: 'effect', label: '马赛克', icon: 'pixelate', desc: '网格马赛克', fields: [
    { name: 'size', label: '马赛克大小', type: 'range', min: 5, max: 50, step: 1, value: 12, show: v => v + 'px' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'swirl', group: 'distort', label: '旋转扭曲', icon: 'rotate', desc: '中心旋涡', fields: [
    { name: 'degrees', label: '旋转角度', type: 'range', min: -180, max: 180, step: 5, value: 30, show: v => v + '°' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'wave', group: 'distort', label: '波浪', icon: 'blur', desc: '正弦波扭曲', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 1, max: 50, step: 1, value: 10, show: v => v + 'px' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'sphere', group: 'distort', label: '球面化', icon: 'circle', desc: '球面映射', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'fisheye', group: 'distort', label: '鱼眼', icon: 'circle', desc: '鱼眼镜头效果', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'mirror', group: 'distort', label: '镜像', icon: 'flip', desc: '左右镜像', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'perspective-tilt', group: 'distort', label: '透视倾斜', icon: 'crop', desc: '透视变形', fields: [
    { name: 'amount', label: '强度', type: 'range', min: -0.5, max: 0.5, step: 0.05, value: 0.2, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'pinch', group: 'distort', label: '挤压', icon: 'vignette', desc: '中心挤压', fields: [
    { name: 'amount', label: '强度', type: 'range', min: -0.5, max: 0.5, step: 0.05, value: 0.2, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'border', group: 'overlay', label: '加边框', icon: 'border', desc: '添加纯色边框', fields: [
    { name: 'width', label: '边框宽度 (px)', type: 'number', value: 10, min: 0, max: 200 },
    { name: 'color', label: '边框颜色', type: 'color', value: '#ffffff' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'round-corners', group: 'overlay', label: '圆角', icon: 'round', desc: '添加圆角 (PNG 透明)', fields: [
    { name: 'radius', label: '圆角半径', type: 'range', min: 0, max: 100, step: 1, value: 20, show: v => v + 'px' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'watermark', group: 'overlay', label: '水印', icon: 'watermark', desc: '添加文字水印', fields: [
    { name: 'text', label: '水印文字', type: 'text', value: 'PIXEL', placeholder: '输入水印文字' },
    { name: 'size', label: '字号', type: 'range', min: 12, max: 96, step: 2, value: 32, show: v => v + 'px' },
    { name: 'color', label: '颜色', type: 'color', value: '#ffffff' },
    { name: 'opacity', label: '透明度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'grid', group: 'overlay', label: '网格', icon: 'pixelate', desc: '网格化叠加', fields: [
    { name: 'spacing', label: '网格间距', type: 'range', min: 10, max: 100, step: 5, value: 30, show: v => v + 'px' },
    { name: 'color', label: '网格颜色', type: 'color', value: '#ffffff' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'mirror-overlay', group: 'overlay', label: '镜像叠加', icon: 'flip', desc: '左右镜像拼接', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'shadow', group: 'overlay', label: '投影', icon: 'vignette', desc: '添加阴影 (PNG 透明)', fields: [
    { name: 'offsetX', label: 'X 偏移', type: 'range', min: -20, max: 20, step: 1, value: 5, show: v => v + 'px' },
    { name: 'offsetY', label: 'Y 偏移', type: 'range', min: -20, max: 20, step: 1, value: 5, show: v => v + 'px' },
    { name: 'blur', label: '模糊半径', type: 'range', min: 0, max: 30, step: 1, value: 8, show: v => v + 'px' },
    { name: 'color', label: '阴影颜色', type: 'color', value: '#000000' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'ascii-art', group: 'overlay', label: 'ASCII 艺术', icon: 'ascii', desc: '转为 ASCII 字符画', fields: [
    { name: 'cols', label: '字符列数', type: 'range', min: 40, max: 200, step: 4, value: 100, show: v => v },
    { name: 'format', label: '输出格式', type: 'select', options: ['txt'], value: 'txt' }
  ]},
  { key: 'thumbnail', group: 'tools', label: '生成缩略图', icon: 'crop', desc: '按尺寸裁剪中心', fields: [
    { name: 'width', label: '宽度', type: 'number', value: 200, min: 10, max: 2000 },
    { name: 'height', label: '高度', type: 'number', value: 200, min: 10, max: 2000 },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','bmp','tiff'], value: 'png' }
  ]},
  { key: 'image-info', group: 'analyze', label: '图片信息', icon: 'info', desc: '读取宽高/类型/模式', fields: [] },
  { key: 'base64', group: 'tools', label: 'Base64 编码', icon: 'base64', desc: '图片转 Base64', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['json'], value: 'json' }
  ]},
  { key: 'average-color', group: 'analyze', label: '平均色', icon: 'info', desc: '整图平均颜色', fields: [] },
  { key: 'brightness-meter', group: 'analyze', label: '亮度计', icon: 'info', desc: '整图亮度统计', fields: [] },
  { key: 'remove-bg', group: 'tools', label: '去除背景', icon: 'crop', desc: '智能识别并移除背景，支持羽化边缘', fields: [
    { name: 'tolerance', label: '容差', type: 'range', min: 0.05, max: 0.8, step: 0.05, value: 0.3, show: v => v.toFixed(2) },
    { name: 'feather', label: '羽化半径', type: 'range', min: 0, max: 10, step: 1, value: 2, show: v => v + 'px' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png'], value: 'png' }
  ]},
  { key: 'upscale-hq', group: 'tools', label: '无损放大', icon: 'resize', desc: '高质量双三次插值放大+自适应锐化', fields: [
    { name: 'scale', label: '放大倍数', type: 'select', options: ['2','3','4'], value: '2' },
    { name: 'sharpness', label: '锐化强度', type: 'range', min: 0, max: 1.5, step: 0.1, value: 0.5, show: v => v.toFixed(1) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','bmp','tiff'], value: 'png' },
    { name: 'quality', label: '质量 (JPEG)', type: 'range', min: 0.3, max: 1, step: 0.05, value: 0.95, show: v => v.toFixed(2) }
  ]},
  { key: 'denoise', group: 'filter', label: '智能降噪', icon: 'blur', desc: '中值滤波降噪，保留边缘细节', fields: [
    { name: 'radius', label: '滤波半径', type: 'range', min: 1, max: 5, step: 1, value: 1, show: v => v + 'px' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'smart-enhance', group: 'effect', label: '画质增强', icon: 'brightness', desc: '自动对比度+饱和度+锐化一键增强', fields: [
    { name: 'amount', label: '增强强度', type: 'range', min: 0.2, max: 1.5, step: 0.1, value: 0.6, show: v => v.toFixed(1) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'ultra-clear', group: 'effect', label: '超清增强', icon: 'sharpen', desc: 'AI 超分(DAT-2)无损放大，让文字物体极致清晰', fields: [
    { name: 'scale', label: '放大倍数', type: 'select', options: ['2','4'], value: '4' },
    { name: 'amount', label: '增强强度(降级)', type: 'range', min: 0.3, max: 2, step: 0.1, value: 0.8, show: v => v.toFixed(1) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','bmp','tiff'], value: 'png' },
    { name: 'quality', label: '质量 (JPEG)', type: 'range', min: 0.3, max: 1, step: 0.05, value: 0.95, show: v => v.toFixed(2) }
  ]}
];

const CATEGORY_LABELS = {
  all: '全部', main: '基础', filter: '滤镜', cinematic: '电影', effect: '特效',
  distort: '变形', overlay: '叠加', tools: '工具', analyze: '分析'
};

const CATEGORY_ICON = {
  all:       '◉', main: '✚', filter: '◐', cinematic: '▶', effect: '⚡',
  distort:   '◎', overlay: '◧', tools: '⚙', analyze: '◌'
};

const ACCENTS = [
  { id: 'indigo',  color: '#6366f1' },
  { id: 'violet',  color: '#8b5cf6' },
  { id: 'pink',    color: '#ec4899' },
  { id: 'rose',    color: '#f43f5e' },
  { id: 'orange',  color: '#f97316' },
  { id: 'amber',   color: '#f59e0b' },
  { id: 'emerald', color: '#10b981' },
  { id: 'teal',    color: '#14b8a6' },
  { id: 'sky',     color: '#0ea5e9' },
  { id: 'blue',    color: '#3b82f6' },
  { id: 'slate',   color: '#64748b' },
  { id: 'mono',    color: '#0f172a' }
];

/* ========== State ========== */
let images = [];
let results = [];
let nextId = 1;
let currentAction = null;
let currentTab = 'all';
let currentImage = null;
let appSettings = { accent: 'indigo', fmt: 'png', quality: 90, dark: false, recent: [] };
try { Object.assign(appSettings, JSON.parse(localStorage.getItem('pixel-settings') || '{}')); } catch(e) {}
document.documentElement.setAttribute('data-theme', appSettings.dark ? 'dark' : 'light');

/* ========== DOM Refs ========== */
const $ = id => document.getElementById(id);

// Landscape
const lCats = $('lCats');
const lTools = $('lTools');
const lImgs = $('lImgs');
const lImgsEmpty = $('lImgsEmpty');
const lImgsStrip = $('lImgsStrip');
const lInfoName = $('lInfoName');
const lInfoType = $('lInfoType');
const lInfoSize = $('lInfoSize');
const lInfoDim = $('lInfoDim');
const lInfoMode = $('lInfoMode');
const lInfoEmpty = $('lInfoEmpty');
const lInfoContent = $('lInfoContent');
const lOutEmpty = $('lOutEmpty');
const lOutContent = $('lOutContent');
const lResList = $('lResList');
const lLog = $('lLog');
const lFootStatus = $('lFootStatus');
const lFootSel = $('lFootSel');
const lFootCount = $('lFootCount');
const lSwatches = $('lSwatches');
const lDarkMode = $('lDarkMode');
const lSetFmt = $('lSetFmt');
const lSetQuality = $('lSetQuality');
const lSetQualityVal = $('lSetQualityVal');
const lAiPath = $('lAiPath');
const lAiStatus = $('lAiStatus');
const toolSearchL = $('toolSearchL');
const btnThemeL = $('btnThemeL');
const btnSettingsL = $('btnSettingsL');
const btnUploadL = $('btnUploadL');
const btnCmdL = $('btnCmdL');
const lBtnClear = $('lBtnClear');
const lBtnDlAll = $('lBtnDlAll');

// Portrait
const pCats = $('pCats');
const pToolList = $('pToolList');
const pFeaturedList = $('pFeaturedList');
const pImgGrid = $('pImgGrid');
const pFileCount = $('pFileCount');
const pResList = $('pResList');
const pOutEmpty = $('pOutEmpty');
const pBtnDlAll = $('pBtnDlAll');
const pSwatches = $('pSwatches');
const pDarkMode = $('pDarkMode');
const pSetFmt = $('pSetFmt');
const pSetQuality = $('pSetQuality');
const pSetQualityVal = $('pSetQualityVal');
const pAiPath = $('pAiPath');
const pAiStatus = $('pAiStatus');
const btnSearchP = $('btnSearchP');
const btnThemeP = $('btnThemeP');
const btnSettingsP = $('btnSettingsP');
const pFab = $('pFab');
const pDrop = $('pDrop');
const pBtnUpload = $('pBtnUpload');
const pFilesEmpty = $('pFilesEmpty');

// Shared
const modalBg = $('modalBg');
const modalIco = $('modalIco');
const modalTitleText = $('modalTitleText');
const modalDesc = $('modalDesc');
const modalScope = $('modalScope');
const modalFields = $('modalFields');
const modalClose = $('modalClose');
const modalCancel = $('modalCancel');
const modalSubmit = $('modalSubmit');
const loading = $('loading');
const loadingText = $('loadingText');
const loadingBar = $('loadingBar');
const cmdBg = $('cmdBg');
const cmdInput = $('cmdInput');
const cmdList = $('cmdList');
const drawer = $('drawer');
const drawerBg = $('drawerBg');
const drawerClose = $('drawerClose');
const toastHost = $('toastHost');
const fileInput = $('fileInput');

/* ========== Helpers ========== */
function humanSize(b) {
  if (!b && b !== 0) return '—';
  if (b < 1024) return b + ' B';
  if (b < 1024 * 1024) return (b / 1024).toFixed(1) + ' KB';
  return (b / 1024 / 1024).toFixed(2) + ' MB';
}
function saveSettings() { try { localStorage.setItem('pixel-settings', JSON.stringify(appSettings)); } catch(e) {} }
function applyAccent() {
  const a = ACCENTS.find(x => x.id === appSettings.accent) || ACCENTS[0];
  document.documentElement.style.setProperty('--accent', a.color);
  document.documentElement.style.setProperty('--accent-2', a.color);
  document.documentElement.style.setProperty('--accent-gradient', `linear-gradient(135deg, ${a.color} 0%, #8b5cf6 50%, #ec4899 100%)`);
}
function logMsg(msg, type) {
  const line = document.createElement('div');
  line.textContent = '[' + new Date().toLocaleTimeString() + '] ' + msg;
  if (type === 'error') line.style.color = 'var(--error)';
  else if (type === 'success') line.style.color = 'var(--success)';
  lLog.appendChild(line);
  lLog.scrollTop = lLog.scrollHeight;
}
function showToast(msg, type) {
  const t = document.createElement('div');
  t.className = 'toast' + (type ? ' ' + type : '');
  t.textContent = msg;
  toastHost.appendChild(t);
  setTimeout(() => { t.style.opacity = '0'; setTimeout(() => t.remove(), 250); }, 2400);
  logMsg(msg, type);
}
function showLoading(text) { loadingText.textContent = text || '处理中...'; loading.classList.add('show'); }
function hideLoading() { loading.classList.remove('show'); }
function setProgress(p) { loadingBar.style.width = (p * 100) + '%'; }

function getSelected() { return images.filter(i => i.selected); }

/* ========== Categories (横屏侧边栏) ========== */
function buildLCats() {
  if (!lCats) return;
  const groups = ['all', ...Array.from(new Set(ACTIONS.map(a => a.group)))];
  lCats.innerHTML = '';
  groups.forEach(g => {
    const count = g === 'all' ? ACTIONS.length : ACTIONS.filter(a => a.group === g).length;
    const btn = document.createElement('button');
    btn.className = 'l-cat' + (g === currentTab ? ' active' : '');
    btn.dataset.group = g;
    btn.innerHTML =
      '<span class="l-cat-ico">' + (CATEGORY_ICON[g] || '·') + '</span>' +
      '<span class="l-cat-name">' + (CATEGORY_LABELS[g] || g) + '</span>' +
      '<span class="l-cat-count">' + count + '</span>';
    btn.addEventListener('click', () => {
      currentTab = g;
      buildLCats();
      renderTools();
    });
    lCats.appendChild(btn);
  });
}

/* ========== Categories (竖屏 chip 行) ========== */
function buildPCats() {
  if (!pCats) return;
  const groups = ['all', ...Array.from(new Set(ACTIONS.map(a => a.group)))];
  pCats.innerHTML = '';
  groups.forEach(g => {
    const count = g === 'all' ? ACTIONS.length : ACTIONS.filter(a => a.group === g).length;
    const btn = document.createElement('button');
    btn.className = 'p-chip' + (g === currentTab ? ' on' : '');
    btn.dataset.group = g;
    btn.innerHTML = (CATEGORY_LABELS[g] || g) + ' <span class="num">' + count + '</span>';
    btn.addEventListener('click', () => {
      currentTab = g;
      buildPCats();
      renderPToolList();
    });
    pCats.appendChild(btn);
  });
}

/* ========== Search (横屏) ========== */
if (toolSearchL) {
  toolSearchL.addEventListener('input', renderTools);
}

/* ========== Tools (横屏) ========== */
function renderTools() {
  if (!lTools) return;
  lTools.innerHTML = '';
  const q = (toolSearchL && toolSearchL.value ? toolSearchL.value : '').trim().toLowerCase();
  let list = currentTab === 'all' ? ACTIONS : ACTIONS.filter(a => a.group === currentTab);
  if (q) {
    list = ACTIONS.filter(a =>
      a.label.toLowerCase().includes(q) ||
      (a.desc || '').toLowerCase().includes(q) ||
      a.key.toLowerCase().includes(q)
    );
  }
  if (currentTab === 'all' && !q) {
    const groups = {};
    list.forEach(a => { (groups[a.group] = groups[a.group] || []).push(a); });
    Object.entries(groups).forEach(([g, items]) => {
      const sec = document.createElement('div');
      sec.className = 'l-tools-section';
      sec.innerHTML =
        '<div class="l-tools-section-head">' +
          '<span class="l-tools-section-title">' + (CATEGORY_ICON[g] || '·') + '  ' + (CATEGORY_LABELS[g] || g) + '</span>' +
          '<span class="l-tools-section-count">' + items.length + ' 项</span>' +
        '</div>' +
        '<div class="l-tool-grid"></div>';
      const grid = sec.querySelector('.l-tool-grid');
      items.forEach(a => grid.appendChild(makeLTool(a)));
      lTools.appendChild(sec);
    });
  } else {
    const sec = document.createElement('div');
    sec.className = 'l-tools-section';
    sec.innerHTML =
      '<div class="l-tools-section-head">' +
        '<span class="l-tools-section-title">' + (CATEGORY_ICON[currentTab] || '·') + '  ' + (CATEGORY_LABELS[currentTab] || currentTab) + '</span>' +
        '<span class="l-tools-section-count">' + list.length + ' 项</span>' +
      '</div>' +
      '<div class="l-tool-grid"></div>';
    const grid = sec.querySelector('.l-tool-grid');
    list.forEach(a => grid.appendChild(makeLTool(a)));
    lTools.appendChild(sec);
  }
}
function makeLTool(a) {
  const card = document.createElement('button');
  card.className = 'l-tool';
  card.dataset.key = a.key;
  card.innerHTML =
    '<div class="l-tool-ico">' + (ICON[a.icon] || '') + '</div>' +
    '<div class="l-tool-name">' + a.label + '</div>' +
    '<div class="l-tool-desc">' + (a.desc || '') + '</div>';
  card.addEventListener('click', () => openModal(a));
  return card;
}

/* ========== Tools (竖屏) ========== */
function renderPToolList() {
  if (!pToolList) return;
  pToolList.innerHTML = '';
  const list = currentTab === 'all' ? ACTIONS : ACTIONS.filter(a => a.group === currentTab);
  list.forEach(a => pToolList.appendChild(makePTool(a)));
}
function makePTool(a) {
  const row = document.createElement('button');
  row.className = 'p-tool';
  row.dataset.key = a.key;
  row.innerHTML =
    '<div class="p-tool-ico">' + (ICON[a.icon] || '') + '</div>' +
    '<div class="p-tool-info">' +
      '<div class="p-tool-name">' + a.label + '</div>' +
      '<div class="p-tool-desc">' + (CATEGORY_LABELS[a.group] || a.group) + ' · ' + (a.desc || '') + '</div>' +
    '</div>' +
    '<div class="p-tool-go"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"><polyline points="9 18 15 12 9 6"/></svg></div>';
  row.addEventListener('click', () => openModal(a));
  return row;
}
function renderPFeatured() {
  if (!pFeaturedList) return;
  pFeaturedList.innerHTML = '';
  const featured = ['convert', 'resize', 'scale', 'rotate', 'grayscale', 'blur', 'sharpen', 'watermark', 'round-corners', 'remove-bg'];
  featured.forEach(k => {
    const a = ACTIONS.find(x => x.key === k);
    if (a) pFeaturedList.appendChild(makePTool(a));
  });
}

/* ========== Modal ========== */
function openModal(action) {
  const selected = getSelected();
  if (selected.length === 0) { showToast('请先选择图片', 'error'); return; }
  currentAction = action;
  modalIco.innerHTML = ICON[action.icon] || '';
  modalTitleText.textContent = action.label;
  modalDesc.textContent = action.desc || '';
  modalScope.textContent = selected.length + ' 张图片 · 批量=' + (selected.length > 1 ? '是' : '否');
  modalFields.innerHTML = '';
  action.fields.forEach((field) => {
    const wrap = document.createElement('div');
    const label = document.createElement('label');
    label.className = 'field-label';
    label.innerHTML = '<span>' + field.label + '</span><span class="v" data-val></span>';
    wrap.appendChild(label);
    if (field.type === 'select') {
      const sel = document.createElement('select');
      sel.className = 'select';
      sel.dataset.field = field.name;
      field.options.forEach(o => {
        const opt = document.createElement('option');
        opt.value = o; opt.textContent = o;
        if (o === field.value) opt.selected = true;
        sel.appendChild(opt);
      });
      wrap.appendChild(sel);
      label.querySelector('[data-val]').textContent = field.value;
      sel.addEventListener('change', () => { label.querySelector('[data-val]').textContent = sel.value; });
    } else if (field.type === 'range') {
      const inp = document.createElement('input');
      inp.type = 'range'; inp.min = field.min; inp.max = field.max; inp.step = field.step; inp.value = field.value;
      inp.dataset.field = field.name;
      const valEl = label.querySelector('[data-val]');
      valEl.textContent = field.show ? field.show(parseFloat(field.value)) : field.value;
      inp.addEventListener('input', () => {
        valEl.textContent = field.show ? field.show(parseFloat(inp.value)) : inp.value;
      });
      wrap.appendChild(inp);
    } else if (field.type === 'color') {
      const inp = document.createElement('input');
      inp.type = 'color';
      inp.style.cssText = 'width:48px;height:32px;border:1px solid var(--border);border-radius:8px;cursor:pointer;background:transparent;padding:2px';
      inp.value = field.value; inp.dataset.field = field.name;
      wrap.appendChild(inp);
      label.querySelector('[data-val]').textContent = field.value;
      inp.addEventListener('input', () => label.querySelector('[data-val]').textContent = inp.value);
    } else {
      const inp = document.createElement('input');
      inp.type = field.type; inp.className = 'input'; inp.value = field.value; inp.dataset.field = field.name;
      if (field.placeholder) inp.placeholder = field.placeholder;
      wrap.appendChild(inp);
      label.querySelector('[data-val]').textContent = field.value;
      inp.addEventListener('input', () => label.querySelector('[data-val]').textContent = inp.value);
    }
    modalFields.appendChild(wrap);
  });
  modalBg.classList.add('show');
}
function closeModal() { modalBg.classList.remove('show'); currentAction = null; }
if (modalClose) modalClose.addEventListener('click', closeModal);
if (modalCancel) modalCancel.addEventListener('click', closeModal);
modalBg.addEventListener('click', e => { if (e.target === modalBg) closeModal(); });

if (modalSubmit) modalSubmit.addEventListener('click', async () => {
  const selected = getSelected();
  if (selected.length === 0 || !currentAction) return;
  const params = {};
  modalFields.querySelectorAll('input, select').forEach(inp => {
    const n = inp.dataset.field; if (!n) return;
    let v = inp.value;
    if (inp.type === 'range' || inp.type === 'number') v = parseFloat(v);
    if (inp.type === 'color') v = v.replace('#', '');
    params[n] = v;
  });
  closeModal();
  results = [];
  if (lResList) lResList.innerHTML = '';
  if (pResList) pResList.innerHTML = '';
  if (lOutContent) lOutContent.style.display = 'none';
  if (lOutEmpty) lOutEmpty.style.display = '';
  if (pBtnDlAll) pBtnDlAll.style.display = 'none';
  if (pOutEmpty) pOutEmpty.style.display = '';
  const multi = selected.length > 1;
  if (multi) { setProgress(0); showLoading('处理中 0/' + selected.length); }
  else { showLoading('处理中...'); }
  try {
    for (let i = 0; i < selected.length; i++) {
      const img = selected[i];
      loadingText.textContent = '处理 ' + (i + 1) + '/' + selected.length + ' · ' + img.file.name;
      const r = await callApi(currentAction.key, params, img.file);
      results.push(r);
      if (multi) setProgress((i + 1) / selected.length);
    }
    hideLoading(); setProgress(0);
    renderResults();
    showToast('完成 · ' + results.length + ' 个文件', 'success');
    // Switch to output pane (portrait)
    switchPPane('output');
    // Update landscape applied state
    document.querySelectorAll('.l-tool, .p-tool').forEach(b => b.classList.toggle('applied', b.dataset.key === currentAction.key));
    addRecent(currentAction.key);
  } catch (err) {
    hideLoading(); setProgress(0);
    showToast('失败: ' + (err.message || err), 'error');
    console.error(err);
  }
});

/* ========== API ========== */
async function callApi(key, params, file) {
  const fd = new FormData();
  fd.append('file', file);
  Object.entries(params).forEach(([k, v]) => fd.append(k, v));
  const resp = await fetch('/api/' + key, { method: 'POST', body: fd });
  if (!resp.ok) { const t = await resp.text(); throw new Error('HTTP ' + resp.status + ': ' + t.substring(0, 200)); }
  const ct = resp.headers.get('Content-Type') || '';
  const cd = resp.headers.get('Content-Disposition') || '';
  const m = cd.match(/filename="?([^"]+)"?/);
  const base = (file.name || 'image').replace(/\.[^.]+$/, '');
  const ext = params.format || 'png';
  if (ct.includes('text/plain')) {
    const text = await resp.text();
    return { name: base + '.txt', blob: new Blob([text], { type: 'text/plain' }), isText: true, text };
  } else if (ct.includes('application/json')) {
    const j = await resp.json();
    return { name: base + '.json', blob: new Blob([JSON.stringify(j, null, 2)], { type: 'application/json' }), isText: true, text: j.base64 || JSON.stringify(j), dataUri: j.dataUri };
  } else {
    const blob = await resp.blob();
    const name = m ? m[1] : (base + '-out.' + ext);
    return { name, blob, isText: false };
  }
}

/* ========== Results ========== */
function renderResults() {
  if (results.length === 0) {
    if (lOutContent) lOutContent.style.display = 'none';
    if (lOutEmpty) lOutEmpty.style.display = '';
    if (pBtnDlAll) pBtnDlAll.style.display = 'none';
    if (pOutEmpty) pOutEmpty.style.display = '';
    return;
  }
  if (lOutContent) lOutContent.style.display = '';
  if (lOutEmpty) lOutEmpty.style.display = 'none';
  if (pBtnDlAll) pBtnDlAll.style.display = '';
  if (pOutEmpty) pOutEmpty.style.display = 'none';
  if (lResList) lResList.innerHTML = '';
  if (pResList) pResList.innerHTML = '';
  results.forEach((r, idx) => {
    let preview = '';
    if (r.dataUri) {
      preview = '<img class="l-res-thumb" src="' + r.dataUri + '">';
    } else if (!r.isText) {
      const url = URL.createObjectURL(r.blob); r.url = url;
      preview = '<img class="l-res-thumb" src="' + url + '">';
    } else {
      preview = '<div class="l-res-thumb" style="display:flex;align-items:center;justify-content:center;background:var(--surface-3);color:var(--text-2);font-size:9px;font-weight:800">TXT</div>';
    }
    // Landscape item
    if (lResList) {
      const item = document.createElement('div');
      item.className = 'l-res-item';
      item.innerHTML =
        preview +
        '<div class="l-res-info"><div class="l-res-name">' + r.name + '</div><div class="l-res-meta">' + (r.blob ? humanSize(r.blob.size) : '—') + '</div></div>' +
        '<button class="l-res-dl" data-idx="' + idx + '"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg></button>';
      lResList.appendChild(item);
    }
    // Portrait item
    if (pResList) {
      const item = document.createElement('div');
      item.className = 'l-res-item';
      item.style.marginBottom = '8px';
      item.innerHTML =
        preview +
        '<div class="l-res-info"><div class="l-res-name">' + r.name + '</div><div class="l-res-meta">' + (r.blob ? humanSize(r.blob.size) : '—') + '</div></div>' +
        '<button class="l-res-dl" data-idx="' + idx + '"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg></button>';
      pResList.appendChild(item);
    }
  });
}
function downloadResult(idx) {
  const r = results[idx]; if (!r) return;
  const a = document.createElement('a');
  if (r.url) a.href = r.url; else a.href = r.dataUri || '#';
  a.download = r.name; document.body.appendChild(a); a.click(); document.body.removeChild(a);
  showToast('已下载: ' + r.name, 'success');
}
function bindResDl(host) {
  if (!host) return;
  host.addEventListener('click', e => {
    const dl = e.target.closest('.l-res-dl');
    if (dl) downloadResult(parseInt(dl.dataset.idx));
  });
}
bindResDl(lResList);
bindResDl(pResList);
async function downloadAll() {
  if (results.length === 0) return;
  for (const r of results) {
    const a = document.createElement('a');
    if (r.url) a.href = r.url; else a.href = r.dataUri || '#';
    a.download = r.name; document.body.appendChild(a); a.click(); document.body.removeChild(a);
    await new Promise(res => setTimeout(res, 200));
  }
  showToast('批量下载 ' + results.length + ' 个文件', 'success');
}
if (lBtnDlAll) lBtnDlAll.addEventListener('click', downloadAll);
if (pBtnDlAll) pBtnDlAll.addEventListener('click', downloadAll);

/* ========== File Upload ========== */
if (fileInput) {
  fileInput.addEventListener('change', e => {
    Array.from(e.target.files).forEach(f => addImage(f));
    fileInput.value = '';
  });
}
function bindDropZone(zone) {
  if (!zone) return;
  zone.addEventListener('click', () => fileInput && fileInput.click());
  ['dragover','dragenter'].forEach(ev => zone.addEventListener(ev, e => {
    e.preventDefault(); zone.style.borderColor = 'var(--accent)';
  }));
  zone.addEventListener('dragleave', () => zone.style.borderColor = '');
  zone.addEventListener('drop', e => {
    e.preventDefault(); zone.style.borderColor = '';
    Array.from(e.dataTransfer.files).filter(f => f.type.startsWith('image/')).forEach(f => addImage(f));
  });
}
bindDropZone(pDrop);
// Make the whole landscape and portrait containers accept drops
['dragover','dragenter'].forEach(ev => document.body.addEventListener(ev, e => e.preventDefault()));
document.body.addEventListener('drop', e => {
  e.preventDefault();
  if (e.dataTransfer && e.dataTransfer.files) {
    Array.from(e.dataTransfer.files).filter(f => f.type.startsWith('image/')).forEach(f => addImage(f));
  }
});

// Top buttons
if (btnUploadL) btnUploadL.addEventListener('click', () => fileInput && fileInput.click());
if (pBtnUpload) pBtnUpload.addEventListener('click', () => fileInput && fileInput.click());
if (pFab) pFab.addEventListener('click', () => fileInput && fileInput.click());

function addImage(file) {
  if (!file.type.startsWith('image/')) { showToast('不支持: ' + file.name, 'error'); return; }
  const id = nextId++;
  const url = URL.createObjectURL(file);
  const img = { id, file, url, selected: true, dims: null };
  images.push(img);
  const tmp = new Image();
  tmp.onload = () => { img.dims = { w: tmp.width, h: tmp.height }; renderImageLists(); };
  tmp.src = url;
  renderImageLists();
  showToast('已添加: ' + file.name, 'success');
}

/* ========== Image Lists (横屏 + 竖屏) ========== */
function renderImageLists() {
  renderLImgs();
  renderPImgs();
  updateFooter();
  // Switch to files pane when first image added (portrait)
  if (images.length === 1) switchPPane('files');
}
function renderLImgs() {
  if (!lImgsStrip) return;
  if (images.length === 0) {
    lImgsEmpty.style.display = '';
    lImgsStrip.style.display = 'none';
    lImgsStrip.innerHTML = '';
    return;
  }
  lImgsEmpty.style.display = 'none';
  lImgsStrip.style.display = '';
  lImgsStrip.innerHTML = '';
  images.forEach(img => {
    const div = document.createElement('div');
    div.className = 'l-img' + (img.selected ? ' sel' : '');
    div.innerHTML = '<img src="' + img.url + '"><button class="l-img-del" data-id="' + img.id + '">×</button>';
    div.addEventListener('click', e => {
      if (e.target.classList.contains('l-img-del')) return;
      img.selected = !img.selected;
      renderImageLists();
    });
    div.addEventListener('dblclick', () => setCurrentImage(img.id));
    lImgsStrip.appendChild(div);
  });
  lImgsStrip.querySelectorAll('.l-img-del').forEach(btn => {
    btn.addEventListener('click', e => {
      e.stopPropagation();
      const id = parseInt(btn.dataset.id);
      const idx = images.findIndex(i => i.id === id);
      if (idx >= 0) {
        URL.revokeObjectURL(images[idx].url);
        images.splice(idx, 1);
        renderImageLists();
      }
    });
  });
}
function renderPImgs() {
  if (!pImgGrid) return;
  pImgGrid.innerHTML = '';
  if (pFileCount) pFileCount.textContent = images.length;
  if (images.length === 0) {
    if (pFilesEmpty) pFilesEmpty.style.display = '';
    pImgGrid.style.display = 'none';
    return;
  }
  if (pFilesEmpty) pFilesEmpty.style.display = 'none';
  pImgGrid.style.display = '';
  images.forEach(img => {
    const div = document.createElement('div');
    div.className = 'p-img-card' + (img.selected ? ' sel' : '');
    div.innerHTML = '<img src="' + img.url + '"><div class="p-img-card-tag">' + (img.dims ? img.dims.w + '×' + img.dims.h : '…') + '</div><button class="p-img-card-del" data-id="' + img.id + '">×</button>';
    div.addEventListener('click', e => {
      if (e.target.classList.contains('p-img-card-del')) return;
      img.selected = !img.selected;
      renderImageLists();
    });
    pImgGrid.appendChild(div);
  });
  pImgGrid.querySelectorAll('.p-img-card-del').forEach(btn => {
    btn.addEventListener('click', e => {
      e.stopPropagation();
      const id = parseInt(btn.dataset.id);
      const idx = images.findIndex(i => i.id === id);
      if (idx >= 0) {
        URL.revokeObjectURL(images[idx].url);
        images.splice(idx, 1);
        renderImageLists();
      }
    });
  });
}

/* ========== Footer (横屏) ========== */
function updateFooter() {
  if (lFootSel) lFootSel.textContent = getSelected().length;
  if (lFootCount) lFootCount.textContent = images.length;
  if (lFootStatus) lFootStatus.textContent = images.length === 0 ? '就绪' : (getSelected().length === 0 ? '未选' : '已就绪');
}
if (lBtnClear) lBtnClear.addEventListener('click', () => {
  if (images.length === 0) return;
  images.forEach(i => URL.revokeObjectURL(i.url));
  images = [];
  renderImageLists();
  showToast('已清空', 'success');
});

/* ========== Inspector (横屏) - 点击缩略图显示信息 ========== */
let currentImageId = null;
function setCurrentImage(id) {
  currentImageId = id;
  const img = images.find(i => i.id === id);
  if (!img) {
    if (lInfoEmpty) lInfoEmpty.style.display = '';
    if (lInfoContent) lInfoContent.style.display = 'none';
    return;
  }
  if (lInfoEmpty) lInfoEmpty.style.display = 'none';
  if (lInfoContent) lInfoContent.style.display = '';
  if (lInfoName) lInfoName.textContent = img.file.name;
  if (lInfoType) lInfoType.textContent = img.file.type || 'image';
  if (lInfoSize) lInfoSize.textContent = humanSize(img.file.size);
  if (lInfoDim) lInfoDim.textContent = img.dims ? (img.dims.w + ' × ' + img.dims.h) : '—';
  if (lInfoMode) lInfoMode.textContent = 'RGB';
  // Switch to info tab
  switchITab('info');
}
// Patch renderLImgs already includes dblclick for inspector
// Patch renderPImgs to add dblclick for inspector (optional)
// (Note: portrait has no inspector panel; dblclick is harmless.)
const _origRenderPImgs = renderPImgs;
renderPImgs = function() {
  _origRenderPImgs();
  if (!pImgGrid) return;
  pImgGrid.querySelectorAll('.p-img-card').forEach(div => {
    const id = parseInt(div.querySelector('.p-img-card-del').dataset.id);
    div.addEventListener('dblclick', () => setCurrentImage(id));
  });
};

/* ========== Inspector Tabs (横屏) ========== */
function switchITab(name) {
  document.querySelectorAll('.l-right-tab').forEach(t => t.classList.toggle('on', t.dataset.itab === name));
  document.querySelectorAll('.l-pane').forEach(p => p.classList.toggle('on', p.dataset.ipane === name));
}
document.querySelectorAll('.l-right-tab').forEach(t => {
  t.addEventListener('click', () => switchITab(t.dataset.itab));
});

/* ========== Portrait Pane Switch ========== */
function switchPPane(name) {
  document.querySelectorAll('.p-nav-item').forEach(t => t.classList.toggle('on', t.dataset.pnav === name));
  document.querySelectorAll('.p-pane').forEach(p => p.classList.toggle('on', p.dataset.ppane === name));
}
document.querySelectorAll('.p-nav-item').forEach(t => {
  t.addEventListener('click', () => switchPPane(t.dataset.pnav));
});

/* ========== Theme Toggle ========== */
function toggleTheme() {
  appSettings.dark = !appSettings.dark;
  document.documentElement.setAttribute('data-theme', appSettings.dark ? 'dark' : 'light');
  if (pDarkMode) pDarkMode.classList.toggle('on', appSettings.dark);
  if (lDarkMode) lDarkMode.classList.toggle('on', appSettings.dark);
  saveSettings();
  showToast(appSettings.dark ? '已切换深色模式' : '已切换浅色模式', 'success');
}
if (btnThemeL) btnThemeL.addEventListener('click', toggleTheme);
if (btnThemeP) btnThemeP.addEventListener('click', toggleTheme);
if (pDarkMode) pDarkMode.addEventListener('click', toggleTheme);
if (lDarkMode) lDarkMode.addEventListener('click', toggleTheme);

/* ========== Settings (Drawer / Settings tab) ========== */
function openDrawer() { if (drawer) drawer.classList.add('show'); if (drawerBg) drawerBg.classList.add('show'); }
function closeDrawer() { if (drawer) drawer.classList.remove('show'); if (drawerBg) drawerBg.classList.remove('show'); }
if (btnSettingsL) btnSettingsL.addEventListener('click', openDrawer);
if (btnSettingsP) btnSettingsP.addEventListener('click', () => switchPPane('settings'));
if (drawerClose) drawerClose.addEventListener('click', closeDrawer);
if (drawerBg) drawerBg.addEventListener('click', closeDrawer);

function buildSwatchGrid(host) {
  if (!host) return;
  host.innerHTML = '';
  ACCENTS.forEach(a => {
    const d = document.createElement('button');
    d.className = 'p-swatch' + (a.id === appSettings.accent ? ' on' : '');
    d.dataset.accent = a.id;
    d.style.background = a.color;
    d.title = a.id;
    d.addEventListener('click', () => {
      appSettings.accent = a.id;
      applyAccent();
      document.querySelectorAll('.p-swatch').forEach(x => x.classList.toggle('on', x === d));
      saveSettings();
      showToast('已切换强调色', 'success');
    });
    host.appendChild(d);
  });
}
buildSwatchGrid(pSwatches);
buildSwatchGrid(lSwatches);

function bindSetFmt(el) {
  if (!el) return;
  el.value = appSettings.fmt;
  el.addEventListener('change', () => {
    appSettings.fmt = el.value;
    if (pSetFmt) pSetFmt.value = el.value;
    if (lSetFmt) lSetFmt.value = el.value;
    saveSettings();
  });
}
bindSetFmt(pSetFmt);
bindSetFmt(lSetFmt);

function bindSetQuality(el, valEl) {
  if (!el || !valEl) return;
  el.value = appSettings.quality;
  valEl.textContent = appSettings.quality;
  el.addEventListener('input', () => {
    appSettings.quality = parseInt(el.value);
    valEl.textContent = appSettings.quality;
    if (pSetQuality) pSetQuality.value = el.value;
    if (lSetQuality) lSetQuality.value = el.value;
    if (pSetQualityVal) pSetQualityVal.textContent = el.value;
    if (lSetQualityVal) lSetQualityVal.textContent = el.value;
    saveSettings();
  });
}
bindSetQuality(pSetQuality, pSetQualityVal);
bindSetQuality(lSetQuality, lSetQualityVal);

// Init dark toggle states
if (pDarkMode) pDarkMode.classList.toggle('on', appSettings.dark);
if (lDarkMode) lDarkMode.classList.toggle('on', appSettings.dark);

/* ========== Command Palette (横屏) ========== */
function openCmd() {
  if (!cmdBg) return;
  cmdBg.classList.add('show');
  cmdInput.value = '';
  cmdInput.focus();
  renderCmdList('');
}
function closeCmd() { if (cmdBg) cmdBg.classList.remove('show'); }
if (btnCmdL) btnCmdL.addEventListener('click', openCmd);
if (btnSearchP) btnSearchP.addEventListener('click', openCmd);
if (cmdBg) cmdBg.addEventListener('click', e => { if (e.target === cmdBg) closeCmd(); });
if (cmdInput) cmdInput.addEventListener('input', e => renderCmdList(e.target.value));
function renderCmdList(q) {
  if (!cmdList) return;
  cmdList.innerHTML = '';
  q = (q || '').toLowerCase().trim();
  if (!q) {
    const common = [
      { icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="17 8 12 3 7 8"/><line x1="12" y1="3" x2="12" y2="15"/></svg>', label: '上传图片', run: () => fileInput && fileInput.click() },
      { icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"/></svg>', label: '打开设置', run: () => { closeCmd(); openDrawer(); } },
      { icon: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"/></svg>', label: appSettings.dark ? '切换浅色' : '切换深色', run: () => { closeCmd(); toggleTheme(); } }
    ];
    common.forEach(c => cmdList.appendChild(makeCmdItem(c.icon, c.label, '', c.run)));
    ACTIONS.slice(0, 12).forEach(a => {
      cmdList.appendChild(makeCmdItem(ICON[a.icon] || '', a.label, CATEGORY_LABELS[a.group] || '', () => {
        closeCmd();
        if (getSelected().length > 0) openModal(a);
        else showToast('请先选择图片', 'error');
      }));
    });
  } else {
    const matched = ACTIONS.filter(a =>
      a.label.toLowerCase().includes(q) ||
      (a.desc || '').toLowerCase().includes(q) ||
      a.key.toLowerCase().includes(q)
    );
    if (matched.length === 0) {
      cmdList.innerHTML = '<div style="padding:24px;text-align:center;color:var(--text-3);font-size:13px">无匹配</div>';
    } else {
      matched.slice(0, 24).forEach(a => {
        cmdList.appendChild(makeCmdItem(ICON[a.icon] || '', a.label, CATEGORY_LABELS[a.group] || '', () => {
          closeCmd();
          if (getSelected().length > 0) openModal(a);
          else showToast('请先选择图片', 'error');
        }));
      });
    }
  }
}
function makeCmdItem(ico, name, key, run) {
  const item = document.createElement('div');
  item.className = 'cmd-item';
  item.innerHTML = '<div class="ico">' + ico + '</div><div class="name">' + name + '</div><div class="key">' + key + '</div>';
  item.addEventListener('click', run);
  return item;
}

/* ========== Keyboard Shortcuts ========== */
document.addEventListener('keydown', e => {
  if ((e.ctrlKey || e.metaKey) && e.key.toLowerCase() === 'k') { e.preventDefault(); openCmd(); }
  if ((e.ctrlKey || e.metaKey) && e.key === ',') { e.preventDefault(); openDrawer(); }
  if (e.key === 'Escape') { closeModal(); closeDrawer(); closeCmd(); }
});

/* ========== Recent (竖屏首页) ========== */
function addRecent(key) {
  appSettings.recent = (appSettings.recent || []).filter(k => k !== key);
  appSettings.recent.unshift(key);
  if (appSettings.recent.length > 8) appSettings.recent = appSettings.recent.slice(0, 8);
  saveSettings();
}

/* ========== AI Model Status ========== */
async function checkAiStatus() {
  try {
    const r = await fetch('/api/ai-status');
    if (r.ok) {
      const j = await r.json();
      const txt = j.loaded ? '✓ 已加载' : '✗ 未加载';
      const cls = j.loaded ? 'badge success' : 'badge';
      const path = j.path || '—';
      if (lAiStatus) { lAiStatus.textContent = txt; lAiStatus.className = cls; }
      if (lAiPath) lAiPath.textContent = path;
      if (pAiStatus) { pAiStatus.textContent = txt; pAiStatus.className = cls; }
      if (pAiPath) pAiPath.textContent = path;
    }
  } catch(e) {}
}

/* ========== Init ========== */
function init() {
  buildLCats();
  buildPCats();
  renderTools();
  renderPToolList();
  renderPFeatured();
  applyAccent();
  checkAiStatus();
  logMsg('PIXEL 已就绪', 'success');
  logMsg('工具数量: ' + ACTIONS.length);
}

init();
