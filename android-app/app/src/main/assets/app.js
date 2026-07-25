/* =========================================================
   PIX_FORGE · Image Workstation - Application Logic
   Brutalist × Cyberpunk Edition
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
  'channel':    '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><rect x="3" y="3" width="7" height="18"/><rect x="14" y="3" width="7" height="18"/></svg>'
};

/* ===== ACTIONS (full list of 126+ tools) ===== */
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
    { name: 'text', label: '水印文字', type: 'text', value: 'PIX_FORGE', placeholder: '输入水印文字' },
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
  all: '全部',
  main: '基础',
  filter: '滤镜',
  cinematic: '电影',
  effect: '特效',
  distort: '变形',
  overlay: '叠加',
  tools: '工具',
  analyze: '分析'
};

const CATEGORY_ICON = {
  all:       '▦',
  main:      '✚',
  filter:    '◉',
  cinematic: '▶',
  effect:    '⚡',
  distort:   '◐',
  overlay:   '◧',
  tools:     '⚙',
  analyze:   '◎'
};

const ACCENTS = [
  { id: 'magenta', color: '#ff00aa' },
  { id: 'cyan',    color: '#00f0ff' },
  { id: 'lime',    color: '#b6ff00' },
  { id: 'yellow',  color: '#ffea00' },
  { id: 'red',     color: '#ff003c' },
  { id: 'orange',  color: '#ff6b00' },
  { id: 'pink',    color: '#ff5cc8' },
  { id: 'blue',    color: '#3b82f6' },
  { id: 'purple',  color: '#a855f7' },
  { id: 'green',   color: '#00ff88' },
  { id: 'teal',    color: '#14b8a6' },
  { id: 'white',   color: '#ededed' }
];

/* ========== State ========== */
let images = [];
let results = [];
let nextId = 1;
let currentAction = null;
let currentTab = 'all';
let currentImage = null;
let appSettings = { accent: 'magenta', fmt: 'png', quality: 90, names: true, zip: true, sfx: false, kbd: true };
try { Object.assign(appSettings, JSON.parse(localStorage.getItem('pixforge-settings') || '{}')); } catch(e) {}

/* ========== DOM Refs ========== */
const $ = id => document.getElementById(id);
const uploadZone = $('uploadZone');
const fileInput = $('fileInput');
const imgList = $('imgList');
const workspace = $('workspace');
const welcomePane = $('welcomePane');
const resultList = $('resultList');
const outputEmpty = $('outputEmpty');
const outputContent = $('outputContent');
const modalBg = $('modalBg');
const modalIco = $('modalIco');
const modalTitleText = $('modalTitleText');
const modalDesc = $('modalDesc');
const modalScopeText = $('modalScopeText');
const modalFields = $('modalFields');
const loading = $('loading');
const loadingText = $('loadingText');
const loadingBar = $('loadingBar');
const nav = $('nav');
const toolsWrap = $('toolsWrap');
const toolSearch = $('toolSearch');
const workInfo = $('workInfo');
const cmdBg = $('cmdBg');
const cmdInput = $('cmdInput');
const cmdList = $('cmdList');
const drawer = $('drawer');
const drawerBg = $('drawerBg');
const toastHost = $('toastHost');
const swatchAccent = $('swatchAccent');
const setFmt = $('setFmt');
const setQuality = $('setQuality');
const setQualityVal = $('setQualityVal');
const footStatus = $('footStatus');
const footCount = $('footCount');
const footSel = $('footSel');
const logList = $('logList');

/* ========== Helpers ========== */
function humanSize(b) {
  if (b < 1024) return b + ' B';
  if (b < 1024 * 1024) return (b / 1024).toFixed(1) + ' KB';
  return (b / 1024 / 1024).toFixed(2) + ' MB';
}
function saveSettings() { try { localStorage.setItem('pixforge-settings', JSON.stringify(appSettings)); } catch(e) {} }
function applyAccent() {
  const a = ACCENTS.find(x => x.id === appSettings.accent) || ACCENTS[0];
  document.documentElement.style.setProperty('--magenta', a.color);
  document.documentElement.style.setProperty('--accent', a.color);
}
function logMsg(msg, type) {
  const line = document.createElement('div');
  line.style.color = type === 'error' ? 'var(--blood)' : (type === 'success' ? 'var(--lime)' : 'var(--bone-dim)');
  line.textContent = '[' + new Date().toLocaleTimeString() + '] ' + msg;
  logList.appendChild(line);
  logList.scrollTop = logList.scrollHeight;
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

/* ========== Sidebar NAV ========== */
function buildNav() {
  const groups = ['all', ...Array.from(new Set(ACTIONS.map(a => a.group)))];
  nav.innerHTML = '';
  groups.forEach((g, i) => {
    const count = g === 'all' ? ACTIONS.length : ACTIONS.filter(a => a.group === g).length;
    const sec = document.createElement('div');
    sec.className = 'nav-section' + (g === currentTab ? ' active' : '');
    sec.dataset.idx = i;
    sec.innerHTML = '<div class="nav-head"><span>' + (CATEGORY_ICON[g] || '▸') + ' ' + (CATEGORY_LABELS[g] || g) + '</span><span class="num">' + count + '</span></div>';
    nav.appendChild(sec);
  });
}
nav.addEventListener('click', e => {
  const sec = e.target.closest('.nav-section');
  if (!sec) return;
  const sections = nav.querySelectorAll('.nav-section');
  const idx = parseInt(sec.dataset.idx);
  const groups = ['all', ...Array.from(new Set(ACTIONS.map(a => a.group)))];
  const g = groups[idx];
  if (!g) return;
  currentTab = g;
  sections.forEach(s => s.classList.toggle('active', s === sec));
  renderTools();
});

/* ========== Search ========== */
toolSearch.addEventListener('input', renderTools);

/* ========== Tools Grid ========== */
function renderTools() {
  toolsWrap.innerHTML = '';
  const q = (toolSearch.value || '').trim().toLowerCase();
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
      const head = document.createElement('div');
      head.className = 'section-head';
      head.innerHTML = '<span class="section-head-text">' + (CATEGORY_ICON[g] || '■') + ' ' + (CATEGORY_LABELS[g] || g) + ' · ' + items.length + '</span><span class="section-head-line"></span>';
      toolsWrap.appendChild(head);
      const row = document.createElement('div');
      row.className = 'tool-row';
      items.forEach(a => row.appendChild(makeToolCard(a)));
      toolsWrap.appendChild(row);
    });
  } else {
    const head = document.createElement('div');
    head.className = 'section-head';
    head.innerHTML = '<span class="section-head-text">' + (CATEGORY_ICON[currentTab] || '■') + ' ' + (CATEGORY_LABELS[currentTab] || currentTab) + ' · ' + list.length + '</span><span class="section-head-line"></span>';
    toolsWrap.appendChild(head);
    const row = document.createElement('div');
    row.className = 'tool-row';
    list.forEach(a => row.appendChild(makeToolCard(a)));
    toolsWrap.appendChild(row);
  }
  workInfo.textContent = list.length + ' / ' + ACTIONS.length;
}
function makeToolCard(a) {
  const card = document.createElement('button');
  card.className = 'tool-card';
  card.dataset.key = a.key;
  card.innerHTML =
    '<div class="tool-ico">' + (ICON[a.icon] || '') + '</div>' +
    '<div class="tool-lbl">' + a.label + '</div>' +
    '<div class="tool-meta">' + (CATEGORY_LABELS[a.group] || a.group) + '</div>';
  card.addEventListener('click', () => openModal(a));
  return card;
}

/* ========== Modal ========== */
function openModal(action) {
  const selected = images.filter(i => i.selected);
  if (selected.length === 0) { showToast('请先选择图片', 'error'); return; }
  currentAction = action;
  modalIco.innerHTML = ICON[action.icon] || '';
  modalTitleText.textContent = action.label;
  modalDesc.textContent = action.desc || '';
  modalScopeText.textContent = '▸ ' + selected.length + ' IMAGE(S) · BATCH=' + (selected.length > 1 ? 'YES' : 'NO');
  modalFields.innerHTML = '';
  action.fields.forEach((field) => {
    const wrap = document.createElement('div');
    wrap.className = 'field';
    const label = document.createElement('label');
    label.className = 'field-label';
    label.innerHTML = '<span>' + field.label + '</span><span class="val" data-val></span>';
    wrap.appendChild(label);
    if (field.type === 'select') {
      const sel = document.createElement('select');
      sel.className = 'field-input';
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
      inp.type = 'color'; inp.className = 'field-input'; inp.value = field.value; inp.dataset.field = field.name;
      wrap.appendChild(inp);
      label.querySelector('[data-val]').textContent = field.value;
      inp.addEventListener('input', () => label.querySelector('[data-val]').textContent = inp.value);
    } else {
      const inp = document.createElement('input');
      inp.type = field.type; inp.className = 'field-input'; inp.value = field.value; inp.dataset.field = field.name;
      if (field.placeholder) inp.placeholder = field.placeholder;
      wrap.appendChild(inp);
      label.querySelector('[data-val]').textContent = field.value;
      inp.addEventListener('input', () => label.querySelector('[data-val]').textContent = inp.value);
    }
    modalFields.appendChild(wrap);
  });
  modalBg.classList.add('show');
}
function closeModal() { modalBg.classList.remove('show'); }
$('modalClose').addEventListener('click', closeModal);
$('modalCancel').addEventListener('click', closeModal);
modalBg.addEventListener('click', e => { if (e.target === modalBg) closeModal(); });
document.addEventListener('keydown', e => { if (e.key === 'Escape') { closeModal(); closeDrawer(); closeCmd(); } });

$('modalSubmit').addEventListener('click', async () => {
  const selected = images.filter(i => i.selected);
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
  results = []; resultList.innerHTML = ''; outputContent.style.display = 'none'; outputEmpty.style.display = '';
  const multi = selected.length > 1;
  if (multi) { setProgress(0); showLoading('PROCESSING 0/' + selected.length); }
  else { showLoading('PROCESSING...'); }
  try {
    for (let i = 0; i < selected.length; i++) {
      const img = selected[i];
      loadingText.textContent = 'PROCESSING ' + (i + 1) + '/' + selected.length + ' · ' + img.file.name;
      const r = await callApi(currentAction.key, params, img.file);
      results.push(r);
      if (multi) setProgress((i + 1) / selected.length);
    }
    hideLoading(); setProgress(0);
    renderResults();
    showToast('DONE · ' + results.length + ' FILE(S)', 'success');
    document.querySelectorAll('.tool-card').forEach(b => b.classList.toggle('applied', b.dataset.key === currentAction.key));
    // Switch to output tab
    document.querySelectorAll('.out-tab').forEach(t => t.classList.toggle('on', t.dataset.itab === 'output'));
    document.querySelectorAll('.out-pane').forEach(p => p.style.display = p.dataset.ipane === 'output' ? '' : 'none');
  } catch (err) {
    hideLoading(); setProgress(0);
    showToast('FAIL: ' + (err.message || err), 'error');
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
  if (results.length === 0) { outputContent.style.display = 'none'; outputEmpty.style.display = ''; return; }
  outputContent.style.display = ''; outputEmpty.style.display = 'none';
  resultList.innerHTML = '';
  results.forEach((r, idx) => {
    const item = document.createElement('div');
    item.className = 'res-item';
    let preview = '';
    if (r.dataUri) {
      preview = '<img class="res-thumb" src="' + r.dataUri + '">';
    } else if (!r.isText) {
      const url = URL.createObjectURL(r.blob); r.url = url;
      preview = '<img class="res-thumb" src="' + url + '">';
    } else {
      preview = '<div class="res-thumb" style="display:flex;align-items:center;justify-content:center;background:var(--ink);color:var(--bone-dim);font-size:10px;font-weight:800">TXT</div>';
    }
    item.innerHTML =
      preview +
      '<div class="res-info"><div class="res-name">' + r.name + '</div><div class="res-meta">' + (r.blob ? humanSize(r.blob.size) : '—') + '</div></div>' +
      '<button class="res-dl" data-idx="' + idx + '">▼</button>';
    resultList.appendChild(item);
  });
}
resultList.addEventListener('click', e => {
  const dl = e.target.closest('.res-dl');
  if (dl) {
    const r = results[parseInt(dl.dataset.idx)];
    if (!r) return;
    const a = document.createElement('a');
    if (r.url) a.href = r.url; else a.href = r.dataUri || '#';
    a.download = r.name; document.body.appendChild(a); a.click(); document.body.removeChild(a);
    showToast('SAVED: ' + r.name, 'success');
  }
});
$('btnDownloadAll').addEventListener('click', async () => {
  if (results.length === 0) return;
  for (const r of results) {
    const a = document.createElement('a');
    if (r.url) a.href = r.url; else a.href = r.dataUri || '#';
    a.download = r.name; document.body.appendChild(a); a.click(); document.body.removeChild(a);
    await new Promise(res => setTimeout(res, 200));
  }
  showToast('BATCH DOWNLOAD ' + results.length + ' FILES', 'success');
});
$('btnDlAll').addEventListener('click', () => $('btnDownloadAll').click());
$('btnAgain').addEventListener('click', () => {
  results = []; resultList.innerHTML = ''; outputContent.style.display = 'none'; outputEmpty.style.display = '';
  showToast('RESULTS CLEARED', 'success');
});

/* ========== File Upload ========== */
uploadZone.addEventListener('click', () => fileInput.click());
$('btnUpload').addEventListener('click', () => fileInput.click());
fileInput.addEventListener('change', e => {
  Array.from(e.target.files).forEach(f => addImage(f));
  fileInput.value = '';
});
['dragover','dragenter'].forEach(ev => uploadZone.addEventListener(ev, e => {
  e.preventDefault(); uploadZone.classList.add('dragover');
}));
uploadZone.addEventListener('dragleave', () => uploadZone.classList.remove('dragover'));
uploadZone.addEventListener('drop', e => {
  e.preventDefault(); uploadZone.classList.remove('dragover');
  Array.from(e.dataTransfer.files).filter(f => f.type.startsWith('image/')).forEach(f => addImage(f));
});

function addImage(file) {
  if (!file.type.startsWith('image/')) { showToast('UNSUPPORTED: ' + file.name, 'error'); return; }
  const id = nextId++;
  const url = URL.createObjectURL(file);
  // Read dims
  const tmp = new Image();
  tmp.onload = () => { img.dims = { w: tmp.width, h: tmp.height }; renderImageList(); };
  const img = { id, file, url, selected: true, dims: null };
  images.push(img);
  tmp.src = url;
  renderImageList();
  showWorkspace();
  showToast('ADDED: ' + file.name, 'success');
}
function showWorkspace() {
  welcomePane.style.display = 'none';
  workspace.style.display = 'flex';
  updateStatus();
}
function renderImageList() {
  imgList.innerHTML = '';
  images.forEach((img, i) => {
    const div = document.createElement('div');
    div.className = 'thumb' + (img.selected ? ' sel' : '');
    div.innerHTML = '<img src="' + img.url + '"><button class="thumb-del" data-id="' + img.id + '">×</button>';
    div.addEventListener('click', e => {
      if (e.target.classList.contains('thumb-del')) return;
      img.selected = !img.selected;
      renderImageList();
      updateStatus();
    });
    imgList.appendChild(div);
  });
  if (images.length === 0) {
    welcomePane.style.display = '';
    workspace.style.display = 'none';
  }
  updateStatus();
}
function updateStatus() {
  const sel = images.filter(i => i.selected).length;
  const total = images.length;
  $('statusInfo').textContent = total + ' IMG · ' + sel + ' SEL';
  footCount.textContent = total;
  footSel.textContent = sel;
  footStatus.textContent = total === 0 ? 'READY' : (sel === 0 ? 'NO SEL' : 'ARMED');
}

imgList.addEventListener('click', e => {
  if (e.target.classList.contains('thumb-del')) {
    e.stopPropagation();
    const id = parseInt(e.target.dataset.id);
    const idx = images.findIndex(i => i.id === id);
    if (idx >= 0) {
      URL.revokeObjectURL(images[idx].url);
      images.splice(idx, 1);
      renderImageList();
    }
  }
});

/* ========== Inspector Tabs ========== */
document.querySelectorAll('.out-tab').forEach(t => {
  t.addEventListener('click', () => {
    document.querySelectorAll('.out-tab').forEach(x => x.classList.toggle('on', x === t));
    document.querySelectorAll('.out-pane').forEach(p => p.style.display = p.dataset.ipane === t.dataset.itab ? '' : 'none');
  });
});

/* ========== Top Bar Buttons ========== */
$('btnCmd').addEventListener('click', openCmd);
$('btnSettings').addEventListener('click', openDrawer);

/* ========== Drawer (Settings) ========== */
function openDrawer() { drawer.classList.add('show'); drawerBg.classList.add('show'); }
function closeDrawer() { drawer.classList.remove('show'); drawerBg.classList.remove('show'); }
$('drawerClose').addEventListener('click', closeDrawer);
drawerBg.addEventListener('click', closeDrawer);

function buildAccentGrid() {
  swatchAccent.innerHTML = '';
  ACCENTS.forEach(a => {
    const d = document.createElement('button');
    d.className = 'swatch' + (a.id === appSettings.accent ? ' on' : '');
    d.dataset.accent = a.id;
    d.style.background = a.color;
    d.title = a.id;
    d.addEventListener('click', () => {
      appSettings.accent = a.id;
      applyAccent();
      document.querySelectorAll('.swatch').forEach(x => x.classList.toggle('on', x === d));
      saveSettings();
    });
    swatchAccent.appendChild(d);
  });
}

setFmt.value = appSettings.fmt;
setFmt.addEventListener('change', () => { appSettings.fmt = setFmt.value; saveSettings(); });
setQuality.value = appSettings.quality;
setQualityVal.textContent = appSettings.quality;
setQuality.addEventListener('input', () => {
  appSettings.quality = parseInt(setQuality.value);
  setQualityVal.textContent = appSettings.quality;
  saveSettings();
});

document.querySelectorAll('.tog').forEach(t => {
  const k = t.dataset.tog;
  if (appSettings[k]) t.classList.add('on');
  t.addEventListener('click', () => {
    t.classList.toggle('on');
    appSettings[k] = t.classList.contains('on');
    saveSettings();
  });
});

/* ========== Command Palette ========== */
function openCmd() {
  cmdBg.classList.add('show');
  cmdInput.value = '';
  cmdInput.focus();
  renderCmdList('');
}
function closeCmd() { cmdBg.classList.remove('show'); }
cmdBg.addEventListener('click', e => { if (e.target === cmdBg) closeCmd(); });
cmdInput.addEventListener('input', e => renderCmdList(e.target.value));
function renderCmdList(q) {
  cmdList.innerHTML = '';
  q = (q || '').toLowerCase().trim();
  if (!q) {
    const common = [
      { key: 'upload', label: '上传图片', icon: '▲', run: () => fileInput.click() },
      { key: 'settings', label: '打开设置', icon: '⚙', run: () => { closeCmd(); openDrawer(); } }
    ];
    common.forEach(c => cmdList.appendChild(makeCmdItem(c.icon, c.label, 'CTRL', c.run)));
    ACTIONS.slice(0, 12).forEach(a => {
      cmdList.appendChild(makeCmdItem(CATEGORY_ICON[a.group] || '■', a.label, CATEGORY_LABELS[a.group] || '', () => {
        closeCmd();
        if (images.filter(i => i.selected).length > 0) openModal(a);
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
      cmdList.innerHTML = '<div style="padding:20px;text-align:center;color:var(--bone-dim);font-size:12px">// NO MATCH</div>';
    } else {
      matched.slice(0, 20).forEach(a => {
        cmdList.appendChild(makeCmdItem(CATEGORY_ICON[a.group] || '■', a.label, CATEGORY_LABELS[a.group] || '', () => {
          closeCmd();
          if (images.filter(i => i.selected).length > 0) openModal(a);
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
  if ((e.ctrlKey || e.metaKey) && e.key === 'k') { e.preventDefault(); openCmd(); }
  if ((e.ctrlKey || e.metaKey) && e.key === ',') { e.preventDefault(); openDrawer(); }
  if (e.key === 'Escape' && cmdBg.classList.contains('show')) closeCmd();
});

/* ========== AI Model Status ========== */
async function checkAiStatus() {
  try {
    const r = await fetch('/api/ai-status');
    if (r.ok) {
      const j = await r.json();
      $('aiStatus').textContent = j.loaded ? '✓ LOADED' : '✗ NOT LOADED';
      $('aiStatus').style.color = j.loaded ? 'var(--lime)' : 'var(--blood)';
      $('aiPath').textContent = j.path || '—';
    }
  } catch(e) {}
}

/* ========== Init ========== */
function init() {
  buildNav();
  renderTools();
  buildAccentGrid();
  applyAccent();
  checkAiStatus();
  logMsg('PIX_FORGE INITIALIZED', 'success');
  logMsg('TOOLS LOADED: ' + ACTIONS.length);
}

init();
