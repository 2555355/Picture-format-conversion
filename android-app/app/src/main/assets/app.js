/* =========================================================
   IMAGE STUDIO · 极简专业版 - 双方向应用逻辑
   - 干净极简 UI · 无玻璃 · 无渐变
   - 横屏 3 栏 + 竖屏卡片流
   ========================================================= */

/* ========== Icons ========== */
const ICON = {
  'convert':    '<svg class="icon" viewBox="0 0 24 24"><polyline points="17 1 21 5 17 9"/><path d="M3 11V9a4 4 0 0 1 4-4h14"/><polyline points="7 23 3 19 7 15"/><path d="M21 13v2a4 4 0 0 1-4 4H3"/></svg>',
  'resize':     '<svg class="icon" viewBox="0 0 24 24"><path d="M3 7V3h4M21 7V3h-4M3 17v4h4M21 17v4h-4"/><path d="M7 12h10"/></svg>',
  'scale':      '<svg class="icon" viewBox="0 0 24 24"><circle cx="11" cy="11" r="7"/><line x1="21" y1="21" x2="16.65" y2="16.65"/><line x1="8" y1="11" x2="14" y2="11"/><line x1="11" y1="8" x2="11" y2="14"/></svg>',
  'rotate':     '<svg class="icon" viewBox="0 0 24 24"><polyline points="23 4 23 10 17 10"/><path d="M20.49 15A9 9 0 1 1 19 6.5L23 10"/></svg>',
  'flip':       '<svg class="icon" viewBox="0 0 24 24"><polyline points="17 1 21 5 17 9"/><path d="M3 11V9a4 4 0 0 1 4-4h14"/><polyline points="7 23 3 19 7 15"/><path d="M21 13v2a4 4 0 0 1-4 4H3"/></svg>',
  'grayscale':  '<svg class="icon" viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="M12 3v18M8 3v18M16 3v18M4 3v18M20 3v18"/></svg>',
  'invert':     '<svg class="icon" viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="M12 3a9 9 0 0 0 0 18z" fill="currentColor"/></svg>',
  'sepia':      '<svg class="icon" viewBox="0 0 24 24"><rect x="3" y="3" width="18" height="18"/><circle cx="12" cy="12" r="4"/></svg>',
  'brightness': '<svg class="icon" viewBox="0 0 24 24"><circle cx="12" cy="12" r="5"/><line x1="12" y1="1" x2="12" y2="3"/><line x1="12" y1="21" x2="12" y2="23"/><line x1="4.22" y1="4.22" x2="5.64" y2="5.64"/><line x1="18.36" y1="18.36" x2="19.78" y2="19.78"/><line x1="1" y1="12" x2="3" y2="12"/><line x1="21" y1="12" x2="23" y2="12"/><line x1="4.22" y1="19.78" x2="5.64" y2="18.36"/><line x1="18.36" y1="5.64" x2="19.78" y2="4.22"/></svg>',
  'blur':       '<svg class="icon" viewBox="0 0 24 24"><circle cx="12" cy="12" r="3"/><circle cx="5" cy="6" r="2"/><circle cx="19" cy="6" r="2"/><circle cx="5" cy="18" r="2"/><circle cx="19" cy="18" r="2"/></svg>',
  'sharpen':    '<svg class="icon" viewBox="0 0 24 24"><polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/></svg>',
  'pixelate':   '<svg class="icon" viewBox="0 0 24 24"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/></svg>',
  'vignette':   '<svg class="icon" viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><circle cx="12" cy="12" r="5" fill="currentColor" opacity="0.3"/></svg>',
  'border':     '<svg class="icon" viewBox="0 0 24 24"><rect x="3" y="3" width="18" height="18"/><rect x="7" y="7" width="10" height="10" fill="currentColor" opacity="0.2"/></svg>',
  'round':      '<svg class="icon" viewBox="0 0 24 24"><rect x="3" y="3" width="18" height="18" rx="8"/></svg>',
  'watermark':  '<svg class="icon" viewBox="0 0 24 24"><path d="M12 2.69l5.66 5.66a8 8 0 1 1-11.31 0z"/></svg>',
  'ascii':      '<svg class="icon" viewBox="0 0 24 24"><polyline points="4 7 4 4 20 4 20 7"/><line x1="9" y1="20" x2="15" y2="20"/><line x1="12" y1="4" x2="12" y2="20"/></svg>',
  'base64':     '<svg class="icon" viewBox="0 0 24 24"><polyline points="16 18 22 12 16 6"/><polyline points="8 6 2 12 8 18"/></svg>',
  'crop':       '<svg class="icon" viewBox="0 0 24 24"><path d="M6 2v14a2 2 0 0 0 2 2h14"/><path d="M18 22V8a2 2 0 0 0-2-2H2"/></svg>',
  'info':       '<svg class="icon" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><line x1="12" y1="16" x2="12" y2="12"/><line x1="12" y1="8" x2="12.01" y2="8"/></svg>',
  'posterize':  '<svg class="icon" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><path d="M12 2v10l5 5"/></svg>',
  'hsb':        '<svg class="icon" viewBox="0 0 24 24"><path d="M12 2C6.5 2 2 6.5 2 12s4.5 10 10 10c.926 0 1.648-.746 1.648-1.688 0-.437-.18-.835-.437-1.125-.29-.289-.438-.652-.438-1.125a1.64 1.64 0 0 1 1.668-1.668h1.996c3.051 0 5.555-2.503 5.555-5.554C21.965 6.012 17.461 2 12 2z"/></svg>',
  'threshold':  '<svg class="icon" viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/><path d="M12 3a9 9 0 0 0 0 18" fill="currentColor"/></svg>',
  'colorbalance':'<svg class="icon" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><path d="M12 2v20M2 12h20"/></svg>',
  'channel':    '<svg class="icon" viewBox="0 0 24 24"><rect x="3" y="3" width="7" height="18"/><rect x="14" y="3" width="7" height="18"/></svg>',
  'circle':     '<svg class="icon" viewBox="0 0 24 24"><circle cx="12" cy="12" r="9"/></svg>',
  'sparkle':    '<svg class="icon" viewBox="0 0 24 24"><path d="M12 2l2.4 7.2L22 12l-7.6 2.8L12 22l-2.4-7.2L2 12l7.6-2.8L12 2z"/></svg>',
  'palette':    '<svg class="icon" viewBox="0 0 24 24"><circle cx="13.5" cy="6.5" r=".5" fill="currentColor"/><path d="M12 2C6.5 2 2 6.5 2 12s4.5 10 10 10c.926 0 1.648-.746 1.648-1.688 0-.437-.18-.835-.437-1.125-.29-.289-.438-.652-.438-1.125a1.64 1.64 0 0 1 1.668-1.668h1.996c3.051 0 5.555-2.503 5.555-5.554C21.965 6.012 17.461 2 12 2z"/></svg>',
  'grid':       '<svg class="icon" viewBox="0 0 24 24"><rect x="3" y="3" width="7" height="7"/><rect x="14" y="3" width="7" height="7"/><rect x="3" y="14" width="7" height="7"/><rect x="14" y="14" width="7" height="7"/></svg>',
  'magic':      '<svg class="icon" viewBox="0 0 24 24"><path d="M15 4V2M15 16v-2M8 9h2M20 9h2M17.8 11.8L19 13M15 9h0M17.8 6.2L19 5M3 21l9-9M12.2 6.2L11 5"/></svg>',
  'enhance':    '<svg class="icon" viewBox="0 0 24 24"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>',
  'noise':      '<svg class="icon" viewBox="0 0 24 24"><path d="M3 12h2M19 12h2M12 3v2M12 19v2M5.6 5.6l1.4 1.4M17 17l1.4 1.4M5.6 18.4L7 17M17 7l1.4-1.4"/><circle cx="12" cy="12" r="3"/></svg>'
};

/* ========== Actions ========== */
const ACTIONS = [
  { key: 'convert',   group: 'main',     label: '格式转换', icon: 'convert', desc: '把图片转换成 PNG / JPG / WEBP / GIF / BMP / TIFF', fields: [
    { name: 'format', label: '目标格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' },
    { name: 'quality', label: '质量 (JPEG/WEBP)', type: 'range', min: 0.3, max: 1, step: 0.05, value: 0.9, show: v => v.toFixed(2) }
  ]},
  { key: 'resize',    group: 'main',     label: '调整尺寸', icon: 'resize', desc: '按指定宽高缩放', fields: [
    { name: 'width', label: '宽度 (px)', type: 'number', value: 800, min: 1, max: 10000 },
    { name: 'height', label: '高度 (px)', type: 'number', value: 600, min: 1, max: 10000 },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'scale',     group: 'main',     label: '等比缩放', icon: 'scale', desc: '按百分比缩放，保持宽高比', fields: [
    { name: 'percent', label: '缩放比例', type: 'range', min: 10, max: 200, step: 5, value: 50, show: v => v + '%' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'rotate',    group: 'main',     label: '旋转', icon: 'rotate', desc: '按任意角度旋转', fields: [
    { name: 'degrees', label: '旋转角度', type: 'range', min: -180, max: 180, step: 1, value: 90, show: v => v + '°' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'flip',      group: 'main',     label: '翻转', icon: 'flip', desc: '水平或垂直翻转', fields: [
    { name: 'direction', label: '翻转方向', type: 'select', options: ['horizontal','vertical'], value: 'horizontal' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'crop',      group: 'main',     label: '裁切', icon: 'crop', desc: '按坐标裁出矩形区域', fields: [
    { name: 'x', label: '起始 X', type: 'number', value: 0, min: 0 },
    { name: 'y', label: '起始 Y', type: 'number', value: 0, min: 0 },
    { name: 'width', label: '宽度', type: 'number', value: 200, min: 1 },
    { name: 'height', label: '高度', type: 'number', value: 200, min: 1 },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'thumbnail', group: 'main',     label: '生成缩略图', icon: 'crop', desc: '按尺寸裁剪中心', fields: [
    { name: 'width', label: '宽度', type: 'number', value: 200, min: 10, max: 2000 },
    { name: 'height', label: '高度', type: 'number', value: 200, min: 10, max: 2000 },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','bmp','tiff'], value: 'png' }
  ]},
  { key: 'grayscale',          group: 'filter', label: '黑白', icon: 'grayscale', desc: '变为灰度照片', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'invert',             group: 'filter', label: '反色', icon: 'invert', desc: '反转为底片效果', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'sepia',              group: 'filter', label: '复古', icon: 'sepia', desc: '怀旧棕色调', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'brightness-contrast',group: 'filter', label: '亮度对比度', icon: 'brightness', desc: '微调亮度和对比度', fields: [
    { name: 'brightness', label: '亮度', type: 'range', min: -1, max: 1, step: 0.05, value: 0.1, show: v => v.toFixed(2) },
    { name: 'contrast', label: '对比度', type: 'range', min: -1, max: 1, step: 0.05, value: 0.1, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'hsb',                group: 'filter', label: '色相饱和度', icon: 'hsb', desc: '调整色相、饱和度、亮度', fields: [
    { name: 'hue', label: '色相', type: 'range', min: -1, max: 1, step: 0.05, value: 0, show: v => v.toFixed(2) },
    { name: 'saturation', label: '饱和度', type: 'range', min: -1, max: 1, step: 0.05, value: 0.1, show: v => v.toFixed(2) },
    { name: 'brightness', label: '亮度', type: 'range', min: -1, max: 1, step: 0.05, value: 0, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'color-balance',      group: 'filter', label: '色彩平衡', icon: 'colorbalance', desc: '调整 RGB 三通道平衡', fields: [
    { name: 'red', label: '红色', type: 'range', min: -100, max: 100, step: 1, value: 0, show: v => v },
    { name: 'green', label: '绿色', type: 'range', min: -100, max: 100, step: 1, value: 0, show: v => v },
    { name: 'blue', label: '蓝色', type: 'range', min: -100, max: 100, step: 1, value: 0, show: v => v },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'gamma',              group: 'filter', label: '伽马校正', icon: 'brightness', desc: '调整图像伽马值', fields: [
    { name: 'gamma', label: '伽马值', type: 'range', min: 0.1, max: 5, step: 0.1, value: 1.0, show: v => v.toFixed(1) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'posterize',          group: 'filter', label: '色调分离', icon: 'posterize', desc: '减少色阶做出色块化', fields: [
    { name: 'levels', label: '色阶数', type: 'range', min: 2, max: 16, step: 1, value: 4, show: v => v },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'threshold',          group: 'filter', label: '二值化', icon: 'threshold', desc: '按阈值转为黑白', fields: [
    { name: 'threshold', label: '阈值', type: 'range', min: 0, max: 255, step: 1, value: 128, show: v => v },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'channel-extract',    group: 'filter', label: '通道提取', icon: 'channel', desc: '提取 R/G/B/Alpha 通道', fields: [
    { name: 'channel', label: '通道', type: 'select', options: ['red','green','blue','alpha'], value: 'red' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','bmp','tiff'], value: 'png' }
  ]},
  { key: 'cool', group: 'cinematic', label: '冷色调', icon: 'colorbalance', desc: '增强蓝青色', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'warm', group: 'cinematic', label: '暖色调', icon: 'colorbalance', desc: '增强红黄色', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'vintage', group: 'cinematic', label: '复古胶片', icon: 'sepia', desc: '老式胶片质感', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.6, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'cinematic', group: 'cinematic', label: '电影感', icon: 'colorbalance', desc: '高对比+色调偏移', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'bw-high-contrast', group: 'cinematic', label: '高对比黑白', icon: 'grayscale', desc: '强对比黑白', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'film-grain', group: 'cinematic', label: '胶片颗粒', icon: 'noise', desc: '叠加胶片噪点', fields: [
    { name: 'amount', label: '颗粒强度', type: 'range', min: 0.05, max: 0.5, step: 0.05, value: 0.15, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'lomo', group: 'cinematic', label: 'LOMO', icon: 'vignette', desc: '暗角+过饱和', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'hdr-look', group: 'cinematic', label: 'HDR 模拟', icon: 'brightness', desc: '模拟 HDR 效果', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'cross-process', group: 'cinematic', label: '交叉冲印', icon: 'colorbalance', desc: '非常规色彩冲印', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'faded', group: 'cinematic', label: '褪色', icon: 'brightness', desc: '降低对比与饱和做褪色', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'vivid', group: 'cinematic', label: '鲜艳', icon: 'hsb', desc: '强化饱和度与对比', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'noir', group: 'cinematic', label: '黑色电影', icon: 'grayscale', desc: '高反差黑白', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'matte', group: 'cinematic', label: '哑光', icon: 'vignette', desc: '哑光低饱和高光', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'punch', group: 'cinematic', label: 'Punch', icon: 'sharpen', desc: '强化色彩与对比', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'fade-warm', group: 'cinematic', label: '暖色褪色', icon: 'sepia', desc: '暖调褪色', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'cartoon', group: 'cinematic', label: '卡通', icon: 'palette', desc: '卡通化效果', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'charcoal', group: 'cinematic', label: '炭笔', icon: 'sharpen', desc: '炭笔素描效果', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'bokeh', group: 'cinematic', label: '散景', icon: 'circle', desc: '添加散景光斑', fields: [
    { name: 'count', label: '光斑数量', type: 'range', min: 5, max: 100, step: 5, value: 30, show: v => v },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'snow', group: 'cinematic', label: '雪花', icon: 'sparkle', desc: '添加飘雪效果', fields: [
    { name: 'intensity', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'rain', group: 'cinematic', label: '雨滴', icon: 'blur', desc: '添加下雨效果', fields: [
    { name: 'intensity', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'sparkles', group: 'cinematic', label: '星光', icon: 'sparkle', desc: '添加星光闪烁', fields: [
    { name: 'intensity', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'blur',   group: 'effect', label: '高斯模糊', icon: 'blur', desc: '高斯模糊滤镜', fields: [
    { name: 'radius', label: '模糊半径', type: 'range', min: 1, max: 50, step: 1, value: 5, show: v => v + 'px' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'sharpen',group: 'effect', label: '锐化', icon: 'sharpen', desc: 'USM 锐化滤镜', fields: [
    { name: 'amount', label: '锐化强度', type: 'range', min: 0, max: 3, step: 0.1, value: 1, show: v => v.toFixed(1) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'pixelate',group: 'effect', label: '像素化', icon: 'pixelate', desc: '马赛克效果', fields: [
    { name: 'size', label: '像素块大小', type: 'range', min: 2, max: 40, step: 1, value: 8, show: v => v + 'px' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'vignette',group: 'effect', label: '暗角', icon: 'vignette', desc: '四边压暗', fields: [
    { name: 'amount', label: '暗角强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'emboss', group: 'effect', label: '浮雕', icon: 'sharpen', desc: '浮雕效果', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 2, step: 0.1, value: 1, show: v => v.toFixed(1) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'edge-detect', group: 'effect', label: '边缘检测', icon: 'crop', desc: '边缘检测', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'oil-paint', group: 'effect', label: '油画', icon: 'sharpen', desc: '油画笔触', fields: [
    { name: 'radius', label: '笔触半径', type: 'range', min: 1, max: 8, step: 1, value: 4, show: v => v + 'px' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'mosaic', group: 'effect', label: '马赛克', icon: 'pixelate', desc: '网格马赛克', fields: [
    { name: 'size', label: '马赛克大小', type: 'range', min: 5, max: 50, step: 1, value: 12, show: v => v + 'px' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'denoise', group: 'effect', label: '智能降噪', icon: 'noise', desc: '中值滤波降噪，保留边缘细节', fields: [
    { name: 'radius', label: '滤波半径', type: 'range', min: 1, max: 5, step: 1, value: 1, show: v => v + 'px' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'smart-enhance', group: 'effect', label: '画质增强', icon: 'enhance', desc: '自动对比度+饱和度+锐化一键增强', fields: [
    { name: 'amount', label: '增强强度', type: 'range', min: 0.2, max: 1.5, step: 0.1, value: 0.6, show: v => v.toFixed(1) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'dehaze', group: 'effect', label: '去雾', icon: 'brightness', desc: '去除图片雾霾', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'beautify', group: 'effect', label: '人像美化', icon: 'enhance', desc: '人像自动美化', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'swirl', group: 'distort', label: '旋转扭曲', icon: 'rotate', desc: '中心旋涡', fields: [
    { name: 'degrees', label: '旋转角度', type: 'range', min: -180, max: 180, step: 5, value: 30, show: v => v + '°' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'wave', group: 'distort', label: '波浪', icon: 'blur', desc: '正弦波扭曲', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 1, max: 50, step: 1, value: 10, show: v => v + 'px' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'sphere', group: 'distort', label: '球面化', icon: 'circle', desc: '球面映射', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'fisheye', group: 'distort', label: '鱼眼', icon: 'circle', desc: '鱼眼镜头效果', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'mirror', group: 'distort', label: '镜像', icon: 'flip', desc: '左右镜像', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'mirror-quad', group: 'distort', label: '四向镜像', icon: 'flip', desc: '上下左右四向镜像拼接', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'kaleidoscope', group: 'distort', label: '万花筒', icon: 'circle', desc: '万花筒效果', fields: [
    { name: 'slices', label: '切片数', type: 'range', min: 2, max: 16, step: 1, value: 6, show: v => v },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'tunnel', group: 'distort', label: '隧道', icon: 'circle', desc: '隧道效果', fields: [
    { name: 'amount', label: '强度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'droste', group: 'distort', label: '德罗斯特', icon: 'circle', desc: '递归嵌套效果', fields: [
    { name: 'levels', label: '嵌套层数', type: 'range', min: 2, max: 8, step: 1, value: 4, show: v => v },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'perspective-tilt', group: 'distort', label: '透视倾斜', icon: 'crop', desc: '透视变形', fields: [
    { name: 'amount', label: '强度', type: 'range', min: -0.5, max: 0.5, step: 0.05, value: 0.2, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'pinch', group: 'distort', label: '挤压', icon: 'vignette', desc: '中心挤压', fields: [
    { name: 'amount', label: '强度', type: 'range', min: -0.5, max: 0.5, step: 0.05, value: 0.2, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'border', group: 'overlay', label: '加边框', icon: 'border', desc: '添加纯色边框', fields: [
    { name: 'width', label: '边框宽度 (px)', type: 'number', value: 10, min: 0, max: 200 },
    { name: 'color', label: '边框颜色', type: 'color', value: '#ffffff' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'gradient-border', group: 'overlay', label: '渐变边框', icon: 'border', desc: '渐变色边框', fields: [
    { name: 'width', label: '边框宽度', type: 'number', value: 10, min: 0, max: 100 },
    { name: 'color1', label: '颜色 1', type: 'color', value: '#ff6b9d' },
    { name: 'color2', label: '颜色 2', type: 'color', value: '#38bdf8' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'round-corners', group: 'overlay', label: '圆角', icon: 'round', desc: '添加圆角 (PNG 透明)', fields: [
    { name: 'radius', label: '圆角半径', type: 'range', min: 0, max: 100, step: 1, value: 20, show: v => v + 'px' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png'], value: 'png' }
  ]},
  { key: 'drop-shadow', group: 'overlay', label: '投影', icon: 'vignette', desc: '添加阴影 (PNG 透明)', fields: [
    { name: 'offsetX', label: 'X 偏移', type: 'range', min: -20, max: 20, step: 1, value: 5, show: v => v + 'px' },
    { name: 'offsetY', label: 'Y 偏移', type: 'range', min: -20, max: 20, step: 1, value: 5, show: v => v + 'px' },
    { name: 'blur', label: '模糊半径', type: 'range', min: 0, max: 30, step: 1, value: 8, show: v => v + 'px' },
    { name: 'color', label: '阴影颜色', type: 'color', value: '#000000' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png'], value: 'png' }
  ]},
  { key: 'outer-glow', group: 'overlay', label: '外发光', icon: 'sparkle', desc: '添加外发光', fields: [
    { name: 'blur', label: '发光范围', type: 'range', min: 1, max: 30, step: 1, value: 8, show: v => v + 'px' },
    { name: 'color', label: '颜色', type: 'color', value: '#ffffff' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png'], value: 'png' }
  ]},
  { key: 'stroke', group: 'overlay', label: '描边', icon: 'border', desc: '添加轮廓描边', fields: [
    { name: 'width', label: '宽度', type: 'range', min: 1, max: 20, step: 1, value: 3, show: v => v + 'px' },
    { name: 'color', label: '颜色', type: 'color', value: '#ffffff' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png'], value: 'png' }
  ]},
  { key: 'watermark-text', group: 'overlay', label: '文字水印', icon: 'watermark', desc: '添加文字水印', fields: [
    { name: 'text', label: '水印文字', type: 'text', value: 'STUDIO', placeholder: '输入水印文字' },
    { name: 'size', label: '字号', type: 'range', min: 12, max: 96, step: 2, value: 32, show: v => v + 'px' },
    { name: 'color', label: '颜色', type: 'color', value: '#ffffff' },
    { name: 'opacity', label: '透明度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.5, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'watermark-diagonal', group: 'overlay', label: '对角水印', icon: 'watermark', desc: '对角线水印', fields: [
    { name: 'text', label: '水印文字', type: 'text', value: 'CONFIDENTIAL', placeholder: '输入水印文字' },
    { name: 'size', label: '字号', type: 'range', min: 24, max: 144, step: 4, value: 64, show: v => v + 'px' },
    { name: 'color', label: '颜色', type: 'color', value: '#ffffff' },
    { name: 'opacity', label: '透明度', type: 'range', min: 0.05, max: 0.5, step: 0.05, value: 0.15, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'watermark-tile', group: 'overlay', label: '平铺水印', icon: 'grid', desc: '平铺水印', fields: [
    { name: 'text', label: '水印文字', type: 'text', value: 'DRAFT', placeholder: '输入水印文字' },
    { name: 'size', label: '字号', type: 'range', min: 12, max: 64, step: 2, value: 24, show: v => v + 'px' },
    { name: 'opacity', label: '透明度', type: 'range', min: 0.05, max: 0.5, step: 0.05, value: 0.1, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'frame-polaroid', group: 'overlay', label: '拍立得边框', icon: 'border', desc: '拍立得相纸边框', fields: [
    { name: 'caption', label: '底注文字', type: 'text', value: '', placeholder: '可选' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp'], value: 'png' }
  ]},
  { key: 'frame-film', group: 'overlay', label: '胶片边框', icon: 'border', desc: '复古胶片边框', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp'], value: 'png' }
  ]},
  { key: 'gradient-overlay', group: 'overlay', label: '渐变叠加', icon: 'colorbalance', desc: '叠加渐变色', fields: [
    { name: 'color1', label: '颜色 1', type: 'color', value: '#000000' },
    { name: 'color2', label: '颜色 2', type: 'color', value: '#ffffff' },
    { name: 'opacity', label: '透明度', type: 'range', min: 0.1, max: 1, step: 0.05, value: 0.4, show: v => v.toFixed(2) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'grid-overlay', group: 'overlay', label: '网格叠加', icon: 'grid', desc: '网格化叠加', fields: [
    { name: 'spacing', label: '网格间距', type: 'range', min: 10, max: 100, step: 5, value: 30, show: v => v + 'px' },
    { name: 'color', label: '网格颜色', type: 'color', value: '#ffffff' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'mirror-overlay', group: 'overlay', label: '镜像叠加', icon: 'flip', desc: '左右镜像拼接', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','gif','bmp','tiff'], value: 'png' }
  ]},
  { key: 'caption', group: 'overlay', label: '添加标题', icon: 'info', desc: '图片底部添加文字', fields: [
    { name: 'text', label: '标题文字', type: 'text', value: 'My Photo', placeholder: '输入标题' },
    { name: 'size', label: '字号', type: 'range', min: 16, max: 64, step: 2, value: 28, show: v => v + 'px' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp'], value: 'png' }
  ]},
  { key: 'timestamp', group: 'overlay', label: '时间戳', icon: 'info', desc: '添加拍摄时间', fields: [
    { name: 'position', label: '位置', type: 'select', options: ['bottom-right','bottom-left','top-right','top-left'], value: 'bottom-right' },
    { name: 'color', label: '颜色', type: 'color', value: '#ffffff' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp'], value: 'png' }
  ]},
  { key: 'shadow', group: 'overlay', label: '投影(旧)', icon: 'vignette', desc: '添加阴影', fields: [
    { name: 'offsetX', label: 'X 偏移', type: 'range', min: -20, max: 20, step: 1, value: 5, show: v => v + 'px' },
    { name: 'offsetY', label: 'Y 偏移', type: 'range', min: -20, max: 20, step: 1, value: 5, show: v => v + 'px' },
    { name: 'blur', label: '模糊半径', type: 'range', min: 0, max: 30, step: 1, value: 8, show: v => v + 'px' },
    { name: 'color', label: '阴影颜色', type: 'color', value: '#000000' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png'], value: 'png' }
  ]},
  { key: 'ascii-art', group: 'overlay', label: 'ASCII 艺术', icon: 'ascii', desc: '转为 ASCII 字符画', fields: [
    { name: 'cols', label: '字符列数', type: 'range', min: 40, max: 200, step: 4, value: 100, show: v => v },
    { name: 'format', label: '输出格式', type: 'select', options: ['txt'], value: 'txt' }
  ]},
  { key: 'image-info', group: 'analyze', label: '图片信息', icon: 'info', desc: '读取宽高/类型/模式', fields: [] },
  { key: 'base64', group: 'analyze', label: 'Base64 编码', icon: 'base64', desc: '图片转 Base64', fields: [
    { name: 'format', label: '输出格式', type: 'select', options: ['json'], value: 'json' }
  ]},
  { key: 'average-color', group: 'analyze', label: '平均色', icon: 'palette', desc: '整图平均颜色', fields: [] },
  { key: 'brightness-meter', group: 'analyze', label: '亮度计', icon: 'brightness', desc: '整图亮度统计', fields: [] },
  { key: 'contrast-meter', group: 'analyze', label: '对比度', icon: 'brightness', desc: '图像对比度分析', fields: [] },
  { key: 'histogram', group: 'analyze', label: '直方图', icon: 'info', desc: 'RGB 通道直方图', fields: [] },
  { key: 'dominant-color', group: 'analyze', label: '主色调', icon: 'palette', desc: '提取主要颜色', fields: [
    { name: 'count', label: '颜色数', type: 'range', min: 3, max: 10, step: 1, value: 5, show: v => v }
  ]},
  { key: 'palette', group: 'analyze', label: '色板', icon: 'palette', desc: '提取色彩调色板', fields: [
    { name: 'count', label: '颜色数', type: 'range', min: 3, max: 10, step: 1, value: 5, show: v => v }
  ]},
  { key: 'hash-md5', group: 'analyze', label: 'MD5', icon: 'info', desc: '图片 MD5 哈希', fields: [] },
  { key: 'hash-sha256', group: 'analyze', label: 'SHA-256', icon: 'info', desc: '图片 SHA-256 哈希', fields: [] },
  { key: 'detect-format', group: 'analyze', label: '格式检测', icon: 'info', desc: '检测实际格式', fields: [] },
  { key: 'hex-dump', group: 'analyze', label: 'Hex 转储', icon: 'info', desc: '前 N 字节的十六进制', fields: [
    { name: 'bytes', label: '字节数', type: 'number', value: 64, min: 16, max: 4096 }
  ]},
  { key: 'remove-bg', group: 'tools', label: '去除背景', icon: 'crop', desc: '智能识别并移除背景', fields: [
    { name: 'tolerance', label: '容差', type: 'range', min: 0.05, max: 0.8, step: 0.05, value: 0.3, show: v => v.toFixed(2) },
    { name: 'feather', label: '羽化半径', type: 'range', min: 0, max: 10, step: 1, value: 2, show: v => v + 'px' },
    { name: 'format', label: '输出格式', type: 'select', options: ['png'], value: 'png' }
  ]},
  { key: 'upscale-hq', group: 'tools', label: '无损放大', icon: 'resize', desc: '高质量双三次插值放大', fields: [
    { name: 'scale', label: '放大倍数', type: 'select', options: ['2','3','4'], value: '2' },
    { name: 'sharpness', label: '锐化强度', type: 'range', min: 0, max: 1.5, step: 0.1, value: 0.5, show: v => v.toFixed(1) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','bmp','tiff'], value: 'png' }
  ]},
  { key: 'ultra-clear', group: 'tools', label: '超清增强', icon: 'enhance', desc: 'AI 超分(DAT-2)无损放大', fields: [
    { name: 'scale', label: '放大倍数', type: 'select', options: ['2','4'], value: '4' },
    { name: 'amount', label: '增强强度', type: 'range', min: 0.3, max: 2, step: 0.1, value: 0.8, show: v => v.toFixed(1) },
    { name: 'format', label: '输出格式', type: 'select', options: ['png','jpeg','webp','bmp','tiff'], value: 'png' }
  ]}
];

/* ========== Categories ========== */
const CATEGORY_LABELS = {
  all: '全部', main: '基础', filter: '滤镜', cinematic: '风格', effect: '特效',
  distort: '变形', overlay: '叠加', tools: 'AI 工具', analyze: '分析'
};
const CATEGORY_ICON = {
  all: '◉', main: '✚', filter: '◐', cinematic: '▶', effect: '⚡',
  distort: '◎', overlay: '◧', tools: '⚙', analyze: '◌'
};

const ACCENTS = [
  { id: 'mono',    color: '#0a0a0a' },
  { id: 'indigo',  color: '#4f46e5' },
  { id: 'violet',  color: '#7c3aed' },
  { id: 'pink',    color: '#ec4899' },
  { id: 'rose',    color: '#f43f5e' },
  { id: 'orange',  color: '#f97316' },
  { id: 'amber',   color: '#f59e0b' },
  { id: 'emerald', color: '#10b981' },
  { id: 'teal',    color: '#14b8a6' },
  { id: 'sky',     color: '#0ea5e9' },
  { id: 'blue',    color: '#3b82f6' },
  { id: 'slate',   color: '#64748b' }
];

/* ========== State ========== */
let images = [];
let results = [];
let nextId = 1;
let currentAction = null;
let currentTab = 'all';
let currentImage = null;
let modalScope = 'current';
let cmdHighlight = 0;
let modalValues = {};

const appSettings = {
  accent: 'mono', dark: false, fmt: 'png', quality: 90,
  naming: 'suffix', autoDl: false, keepOrig: true,
  compact: false, showInfo: true, concurrency: 2
};
try { Object.assign(appSettings, JSON.parse(localStorage.getItem('studio-settings') || '{}')); } catch(e) {}
document.documentElement.setAttribute('data-theme', appSettings.dark ? 'dark' : 'light');

/* ========== DOM Refs ========== */
const $ = id => document.getElementById(id);

// Landscape
const lNav = $('lNav');
const lTools = $('lTools');
const lCanvas = $('lCanvas');
const lCanvasEmpty = $('lCanvasEmpty');
const lCanvasStrip = $('lCanvasStrip');
const lStatTools = $('lStatTools');
const lToolsTitle = $('lToolsTitle');
const lToolsCount = $('lToolsCount');
const lResBadge = $('lResBadge');
const lInfoName = $('lInfoName');
const lInfoType = $('lInfoType');
const lInfoSize = $('lInfoSize');
const lInfoDim = $('lInfoDim');
const lInfoMod = $('lInfoMod');
const lInfoThumb = $('lInfoThumb');
const lInfoEmpty = $('lInfoEmpty');
const lInfoContent = $('lInfoContent');
const lOutEmpty = $('lOutEmpty');
const lOutContent = $('lOutContent');
const lResList = $('lResList');
const lLog = $('lLog');
const searchL = $('searchL');
const btnThemeL = $('btnThemeL');
const btnSettingsL = $('btnSettingsL');
const btnUploadL = $('btnUploadL');
const lBtnClear = $('lBtnClear');
const lBtnHelp = $('lBtnHelp');
const lBtnDlAll = $('lBtnDlAll');

// Portrait
const pCats = $('pCats');
const pToolList = $('pToolList');
const pHeroEmpty = $('pHeroEmpty');
const pHeroStrip = $('pHeroStrip');
const pToolsTitle = $('pToolsTitle');
const pToolsSub = $('pToolsSub');
const pResultSection = $('pResultSection');
const pResCount = $('pResCount');
const pResList = $('pResList');
const btnThemeP = $('btnThemeP');
const btnSettingsP = $('btnSettingsP');
const btnUploadP = $('btnUploadP');
const pBtnPick = $('pBtnPick');
const pBtnDlAll = $('pBtnDlAll');

// Shared
const modalBg = $('modalBg');
const modalIco = $('modalIco');
const modalTitle = $('modalTitle');
const modalSub = $('modalSub');
const modalFields = $('modalFields');
const modalClose = $('modalClose');
const modalCancel = $('modalCancel');
const modalSubmit = $('modalSubmit');
const modalScopeEl = $('modalScope');
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
const helpBg = $('helpBg');
const helpClose = $('helpClose');
const helpOk = $('helpOk');

// Settings
const darkToggle = $('darkToggle');
const swatches = $('swatches');
const compactToggle = $('compactToggle');
const infoToggle = $('infoToggle');
const setFmt = $('setFmt');
const setQuality = $('setQuality');
const setQualityVal = $('setQualityVal');
const setNaming = $('setNaming');
const autoDlToggle = $('autoDlToggle');
const keepOrigToggle = $('keepOrigToggle');
const setConcurrency = $('setConcurrency');
const aiStatus = $('aiStatus');
const aiPath = $('aiPath');
const aiTag = $('aiTag');
const aiFallback = $('aiFallback');
const aboutCount = $('aboutCount');

/* ========== Helpers ========== */
function humanSize(b) {
  if (!b && b !== 0) return '—';
  if (b < 1024) return b + ' B';
  if (b < 1024 * 1024) return (b / 1024).toFixed(1) + ' KB';
  return (b / 1024 / 1024).toFixed(2) + ' MB';
}
function humanDate(ts) {
  if (!ts) return '—';
  const d = new Date(ts);
  return d.getFullYear() + '-' + String(d.getMonth()+1).padStart(2,'0') + '-' + String(d.getDate()).padStart(2,'0') + ' ' + String(d.getHours()).padStart(2,'0') + ':' + String(d.getMinutes()).padStart(2,'0');
}
function saveSettings() { try { localStorage.setItem('studio-settings', JSON.stringify(appSettings)); } catch(e) {} }
function applyAccent() {
  const a = ACCENTS.find(x => x.id === appSettings.accent) || ACCENTS[0];
  document.documentElement.style.setProperty('--accent', a.color);
  document.documentElement.style.setProperty('--accent-fg', appSettings.dark ? '#0a0a0a' : '#ffffff');
}
function logMsg(msg, type) {
  if (!lLog) return;
  const line = document.createElement('div');
  line.className = 'l-log-line' + (type ? ' ' + type : '');
  line.innerHTML = '<span class="t">' + new Date().toLocaleTimeString() + '</span>' + msg;
  lLog.appendChild(line);
  lLog.scrollTop = lLog.scrollHeight;
  while (lLog.children.length > 200) lLog.removeChild(lLog.firstChild);
}
function showToast(msg, type) {
  const t = document.createElement('div');
  t.className = 'toast' + (type ? ' ' + type : '');
  t.textContent = msg;
  toastHost.appendChild(t);
  setTimeout(() => { t.style.opacity = '0'; t.style.transition = 'opacity 200ms'; setTimeout(() => t.remove(), 200); }, 2400);
  logMsg(msg, type);
}
function showLoading(text) { loadingText.textContent = text || '处理中...'; loading.classList.add('show'); loadingBar.style.width = '0%'; }
function hideLoading() { loading.classList.remove('show'); }
function setProgress(p) { loadingBar.style.width = (p * 100) + '%'; }
function basename(name) {
  const i = name.lastIndexOf('.');
  return i > 0 ? name.substring(0, i) : name;
}
function ext(name) {
  const i = name.lastIndexOf('.');
  return i > 0 ? name.substring(i+1).toLowerCase() : 'png';
}
function makeResultName(origName, fmt) {
  const base = basename(origName);
  const targetExt = (fmt || 'png').replace('jpeg','jpg');
  if (appSettings.naming === 'prefix') return 'studio_' + base + '.' + targetExt;
  if (appSettings.naming === 'timestamp') {
    const ts = new Date().toISOString().replace(/[:.]/g,'').replace('T','_').substring(0,15);
    return base + '_' + ts + '.' + targetExt;
  }
  return base + '_out.' + targetExt;
}

/* ========== Categories (横屏侧边栏) ========== */
function buildLCats() {
  if (!lNav) return;
  const groups = ['all', ...Array.from(new Set(ACTIONS.map(a => a.group)))];
  lNav.innerHTML = '';
  groups.forEach(g => {
    const count = g === 'all' ? ACTIONS.length : ACTIONS.filter(a => a.group === g).length;
    const btn = document.createElement('button');
    btn.className = 'l-nav-item' + (g === currentTab ? ' on' : '');
    btn.innerHTML =
      '<span class="l-nav-ico">' + (CATEGORY_ICON[g] || '·') + '</span>' +
      '<span class="l-nav-name">' + (CATEGORY_LABELS[g] || g) + '</span>' +
      '<span class="l-nav-count">' + count + '</span>';
    btn.addEventListener('click', () => {
      currentTab = g;
      buildLCats();
      renderTools();
    });
    lNav.appendChild(btn);
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
    btn.className = 'p-cat' + (g === currentTab ? ' on' : '');
    btn.innerHTML = (CATEGORY_LABELS[g] || g) + ' <span class="n">' + count + '</span>';
    btn.addEventListener('click', () => {
      currentTab = g;
      buildPCats();
      renderPToolList();
    });
    pCats.appendChild(btn);
  });
}

/* ========== Tools (横屏) ========== */
function renderTools() {
  if (!lTools) return;
  const search = (searchL ? searchL.value : '').toLowerCase().trim();
  const list = ACTIONS.filter(a => {
    if (currentTab !== 'all' && a.group !== currentTab) return false;
    if (search && !a.label.toLowerCase().includes(search) && !a.desc.toLowerCase().includes(search)) return false;
    return true;
  });
  if (lToolsTitle) lToolsTitle.textContent = search ? '搜索: ' + search : (CATEGORY_LABELS[currentTab] || '所有工具');
  if (lToolsCount) lToolsCount.textContent = list.length + ' 项';
  lTools.innerHTML = '';
  list.forEach(a => {
    const div = document.createElement('button');
    div.className = 'l-tool';
    div.innerHTML =
      '<div class="l-tool-ico">' + (ICON[a.icon] || ICON.info) + '</div>' +
      '<div style="flex:1;min-width:0">' +
        '<div class="l-tool-name">' + a.label + '</div>' +
        '<div class="l-tool-desc" style="white-space:nowrap;overflow:hidden;text-overflow:ellipsis">' + a.desc + '</div>' +
      '</div>';
    div.addEventListener('click', () => openModal(a));
    lTools.appendChild(div);
  });
}

/* ========== Tools (竖屏) ========== */
function renderPToolList() {
  if (!pToolList) return;
  const list = ACTIONS.filter(a => currentTab === 'all' || a.group === currentTab);
  if (pToolsTitle) pToolsTitle.textContent = CATEGORY_LABELS[currentTab] || '所有工具';
  if (pToolsSub) pToolsSub.textContent = list.length + ' 项 · 点击使用';
  pToolList.innerHTML = '';
  list.forEach(a => {
    const div = document.createElement('button');
    div.className = 'l-tool';
    div.innerHTML =
      '<div class="l-tool-ico p-tool-ico">' + (ICON[a.icon] || ICON.info) + '</div>' +
      '<div style="flex:1;min-width:0">' +
        '<div class="l-tool-name">' + a.label + '</div>' +
        '<div class="l-tool-desc">' + a.desc + '</div>' +
      '</div>' +
      '<svg class="icon" viewBox="0 0 24 24" style="color:var(--text-3)"><polyline points="9 18 15 12 9 6"/></svg>';
    div.addEventListener('click', () => openModal(a));
    pToolList.appendChild(div);
  });
}

/* ========== Image Strip ========== */
function renderStrips() {
  // Landscape
  if (lCanvasEmpty) lCanvasEmpty.style.display = images.length ? 'none' : '';
  if (lCanvasStrip) {
    lCanvasStrip.style.display = images.length ? 'flex' : 'none';
    lCanvasStrip.innerHTML = '';
    images.forEach(img => {
      const t = document.createElement('div');
      t.className = 'l-thumb' + (currentImage && currentImage.id === img.id ? ' on' : '');
      t.innerHTML = '<img src="' + img.dataUrl + '" alt=""><div class="l-thumb-name">' + img.name + '</div><div class="l-thumb-x">×</div>';
      t.addEventListener('click', (e) => {
        if (e.target.classList.contains('l-thumb-x')) { removeImage(img.id); return; }
        selectImage(img.id);
      });
      lCanvasStrip.appendChild(t);
    });
  }
  // Portrait
  if (pHeroEmpty) pHeroEmpty.style.display = images.length ? 'none' : '';
  if (pHeroStrip) {
    pHeroStrip.style.display = images.length ? 'flex' : 'none';
    pHeroStrip.innerHTML = '';
    images.forEach(img => {
      const t = document.createElement('div');
      t.className = 'l-thumb' + (currentImage && currentImage.id === img.id ? ' on' : '');
      t.innerHTML = '<img src="' + img.dataUrl + '" alt=""><div class="l-thumb-name">' + img.name + '</div><div class="l-thumb-x">×</div>';
      t.addEventListener('click', (e) => {
        if (e.target.classList.contains('l-thumb-x')) { removeImage(img.id); return; }
        selectImage(img.id);
      });
      pHeroStrip.appendChild(t);
    });
  }
}

function selectImage(id) {
  currentImage = images.find(i => i.id === id) || null;
  renderStrips();
  renderInfo();
}

function removeImage(id) {
  images = images.filter(i => i.id !== id);
  results = results.filter(r => r.sourceId !== id);
  if (currentImage && currentImage.id === id) currentImage = images[0] || null;
  renderStrips();
  renderInfo();
  renderResults();
  logMsg('已删除图片');
}

function clearAll() {
  if (!images.length) return;
  if (!confirm('清空所有图片和结果？')) return;
  images = [];
  results = [];
  currentImage = null;
  renderStrips();
  renderInfo();
  renderResults();
  showToast('已清空', 'success');
}

/* ========== Info Panel ========== */
function renderInfo() {
  if (!lInfoEmpty) return;
  if (!currentImage) {
    lInfoEmpty.style.display = '';
    lInfoContent.style.display = 'none';
    return;
  }
  lInfoEmpty.style.display = 'none';
  lInfoContent.style.display = '';
  lInfoThumb.src = currentImage.dataUrl;
  lInfoName.textContent = currentImage.name;
  lInfoType.textContent = (currentImage.type || '—').toUpperCase();
  lInfoSize.textContent = humanSize(currentImage.size);
  lInfoDim.textContent = currentImage.width + ' × ' + currentImage.height;
  lInfoMod.textContent = humanDate(currentImage.lastModified);
}

/* ========== Results ========== */
function renderResults() {
  // Landscape
  if (lOutEmpty) lOutEmpty.style.display = results.length ? 'none' : '';
  if (lOutContent) lOutContent.style.display = results.length ? 'block' : 'none';
  if (lResBadge) lResBadge.textContent = results.length;
  if (lResList) {
    lResList.innerHTML = '';
    results.forEach(r => {
      const div = document.createElement('div');
      div.className = 'l-result';
      div.innerHTML =
        '<img src="' + r.dataUrl + '" alt="">' +
        '<div class="l-result-info">' +
          '<div class="l-result-name">' + r.name + '</div>' +
          '<div class="l-result-meta">' + humanSize(r.size) + ' · ' + (r.dim || '') + '</div>' +
        '</div>' +
        '<div class="l-result-action">' +
          '<button class="btn btn-sm" data-act="dl" title="下载"><svg class="icon" viewBox="0 0 24 24"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg></button>' +
        '</div>';
      div.querySelector('[data-act=dl]').addEventListener('click', () => downloadOne(r));
      lResList.appendChild(div);
    });
  }
  // Portrait
  if (pResultSection) pResultSection.style.display = results.length ? '' : 'none';
  if (pResCount) pResCount.textContent = results.length;
  if (pResList) {
    pResList.innerHTML = '';
    results.forEach(r => {
      const div = document.createElement('div');
      div.className = 'l-result';
      div.innerHTML =
        '<img src="' + r.dataUrl + '" alt="">' +
        '<div class="l-result-info">' +
          '<div class="l-result-name">' + r.name + '</div>' +
          '<div class="l-result-meta">' + humanSize(r.size) + '</div>' +
        '</div>' +
        '<button class="btn btn-sm" data-act="dl" title="下载"><svg class="icon" viewBox="0 0 24 24"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/><polyline points="7 10 12 15 17 10"/><line x1="12" y1="15" x2="12" y2="3"/></svg></button>';
      div.querySelector('[data-act=dl]').addEventListener('click', () => downloadOne(r));
      pResList.appendChild(div);
    });
  }
}

function downloadOne(r) {
  const a = document.createElement('a');
  a.href = r.dataUrl;
  a.download = r.name;
  document.body.appendChild(a);
  a.click();
  a.remove();
  logMsg('已下载: ' + r.name, 'ok');
}

async function downloadAll() {
  if (!results.length) return showToast('暂无结果可下载', 'error');
  if (results.length === 1) { downloadOne(results[0]); return; }
  showLoading('打包 ZIP...');
  setProgress(0.1);
  // Try server zip endpoint first
  try {
    const form = new FormData();
    results.forEach(r => form.append('files', dataURLtoBlob(r.dataUrl), r.name));
    const res = await fetch('/api/zip-download', { method: 'POST', body: form });
    setProgress(0.7);
    if (res.ok) {
      const blob = await res.blob();
      const a = document.createElement('a');
      a.href = URL.createObjectURL(blob);
      a.download = 'studio_results_' + Date.now() + '.zip';
      a.click();
      URL.revokeObjectURL(a.href);
      hideLoading();
      showToast('已下载 ZIP', 'success');
      logMsg('已下载 ZIP (' + results.length + ' 个文件)', 'ok');
      return;
    }
  } catch(e) {}
  // Fallback: download one by one
  hideLoading();
  showToast('服务器打包失败，逐个下载', 'error');
  results.forEach((r, i) => setTimeout(() => downloadOne(r), i * 200));
}

function dataURLtoBlob(d) {
  const a = d.split(',');
  const m = a[0].match(/:(.*?);/)[1];
  const bin = atob(a[1]);
  const u8 = new Uint8Array(bin.length);
  for (let i = 0; i < bin.length; i++) u8[i] = bin.charCodeAt(i);
  return new Blob([u8], { type: m });
}

/* ========== File Upload ========== */
function bindUpload(btn) {
  if (!btn) return;
  btn.addEventListener('click', () => fileInput.click());
}
async function addFiles(files) {
  const list = Array.from(files).filter(f => f.type.startsWith('image/'));
  if (!list.length) { showToast('请选择图片文件', 'error'); return; }
  showLoading('读取 ' + list.length + ' 张图片...');
  let i = 0;
  for (const f of list) {
    try {
      const { dataUrl, width, height, type } = await readImage(f);
      images.push({ id: nextId++, name: f.name, size: f.size, type, dataUrl, width, height, lastModified: f.lastModified });
      i++;
      setProgress(i / list.length);
    } catch(e) {
      logMsg('读取失败: ' + f.name + ' - ' + e.message, 'err');
    }
  }
  if (!currentImage && images.length) currentImage = images[0];
  renderStrips();
  renderInfo();
  hideLoading();
  showToast('已添加 ' + i + ' 张图片', 'success');
  logMsg('添加 ' + i + ' 张图片', 'ok');
}
function readImage(file) {
  return new Promise((resolve, reject) => {
    const r = new FileReader();
    r.onload = () => {
      const img = new Image();
      img.onload = () => resolve({ dataUrl: r.result, width: img.naturalWidth, height: img.naturalHeight, type: file.type });
      img.onerror = () => reject(new Error('图片解析失败'));
      img.src = r.result;
    };
    r.onerror = () => reject(new Error('文件读取失败'));
    r.readAsDataURL(file);
  });
}

/* ========== Modal: Tool Options ========== */
function openModal(action) {
  currentAction = action;
  modalScope = 'current';
  modalValues = {};
  action.fields.forEach(f => { modalValues[f.name] = f.value; });
  if (modalIco) modalIco.innerHTML = ICON[action.icon] || ICON.info;
  if (modalTitle) modalTitle.textContent = action.label;
  if (modalSub) modalSub.textContent = action.desc;
  // Scope pills
  if (modalScopeEl) {
    modalScopeEl.style.display = (action.fields.length > 0) ? 'flex' : 'none';
    modalScopeEl.querySelectorAll('.scope-pill').forEach(p => p.classList.toggle('on', p.dataset.scope === modalScope));
  }
  // Fields
  if (modalFields) {
    modalFields.innerHTML = '';
    if (!action.fields.length) {
      modalFields.innerHTML = '<div style="text-align:center;padding:24px;color:var(--text-3);font-size:13px">此工具无需参数</div>';
    } else {
      action.fields.forEach(f => {
        const wrap = document.createElement('div');
        wrap.className = 'field';
        if (f.type === 'range') {
          wrap.innerHTML =
            '<label class="field-lbl">' + f.label + '</label>' +
            '<div class="field-range">' +
              '<input type="range" name="' + f.name + '" min="' + f.min + '" max="' + f.max + '" step="' + f.step + '" value="' + f.value + '">' +
              '<span class="field-range-val">' + (f.show ? f.show(f.value) : f.value) + '</span>' +
            '</div>';
        } else if (f.type === 'select') {
          wrap.innerHTML =
            '<label class="field-lbl">' + f.label + '</label>' +
            '<select class="field-input" name="' + f.name + '">' +
              f.options.map(o => '<option value="' + o + '"' + (o === f.value ? ' selected' : '') + '>' + o.toUpperCase() + '</option>').join('') +
            '</select>';
        } else if (f.type === 'number') {
          wrap.innerHTML =
            '<label class="field-lbl">' + f.label + '</label>' +
            '<input class="field-input" type="number" name="' + f.name + '" value="' + f.value + '" min="' + (f.min || 0) + '" max="' + (f.max || 99999) + '">';
        } else if (f.type === 'color') {
          wrap.innerHTML =
            '<label class="field-lbl">' + f.label + '</label>' +
            '<input class="field-input" type="color" name="' + f.name + '" value="' + f.value + '" style="height:40px;padding:4px">';
        } else if (f.type === 'text') {
          wrap.innerHTML =
            '<label class="field-lbl">' + f.label + '</label>' +
            '<input class="field-input" type="text" name="' + f.name + '" value="' + f.value + '" placeholder="' + (f.placeholder || '') + '">';
        } else {
          wrap.innerHTML =
            '<label class="field-lbl">' + f.label + '</label>' +
            '<input class="field-input" type="text" name="' + f.name + '" value="' + f.value + '">';
        }
        modalFields.appendChild(wrap);
      });
      // bind live update for range
      modalFields.querySelectorAll('input[type=range]').forEach(inp => {
        inp.addEventListener('input', () => {
          const f = action.fields.find(x => x.name === inp.name);
          const valSpan = inp.parentElement.querySelector('.field-range-val');
          if (valSpan && f && f.show) valSpan.textContent = f.show(parseFloat(inp.value));
        });
      });
    }
  }
  modalBg.classList.add('show');
  // Focus first field
  setTimeout(() => {
    const f = modalFields && modalFields.querySelector('input,select,textarea');
    if (f && f.type !== 'color') f.focus();
  }, 100);
}
function closeModal() {
  modalBg.classList.remove('show');
  currentAction = null;
}
async function submitModal() {
  if (!currentAction) return;
  // Collect values
  const vals = {};
  currentAction.fields.forEach(f => {
    const el = modalFields.querySelector('[name=' + f.name + ']');
    if (!el) return;
    let v = el.value;
    if (f.type === 'range' || f.type === 'number') v = parseFloat(v);
    if (f.type === 'text' && f.name === 'text' && !v) v = f.value;
    vals[f.name] = v;
  });
  // Determine targets
  let targets;
  if (modalScope === 'all' && currentAction.fields.length) {
    targets = images;
  } else {
    if (!currentImage) { showToast('请先选择图片', 'error'); return; }
    targets = [currentImage];
  }
  if (!targets.length) { showToast('请先添加图片', 'error'); return; }
  closeModal();
  showLoading(currentAction.label + ' · 0/' + targets.length);
  let done = 0, ok = 0;
  for (const img of targets) {
    try {
      const r = await runAction(currentAction, img, vals);
      results.push(r);
      ok++;
      if (appSettings.autoDl && targets.length === 1) downloadOne(r);
    } catch(e) {
      logMsg('处理失败: ' + img.name + ' - ' + e.message, 'err');
    }
    done++;
    setProgress(done / targets.length);
    loadingText.textContent = currentAction.label + ' · ' + done + '/' + targets.length;
  }
  hideLoading();
  renderResults();
  if (ok) showToast('处理完成 · ' + ok + '/' + targets.length, 'success');
  else showToast('处理失败', 'error');
  logMsg('完成: ' + currentAction.label + ' · 成功 ' + ok + '/' + targets.length, ok ? 'ok' : 'err');
  // Switch to output tab on landscape
  if (window.matchMedia('(orientation: landscape)').matches && results.length) {
    document.querySelectorAll('.l-tab').forEach(t => t.classList.toggle('on', t.dataset.ltab === 'output'));
    document.querySelectorAll('.l-pane').forEach(p => p.classList.toggle('on', p.dataset.lpane === 'output'));
  }
}
async function runAction(action, img, vals) {
  // For analyze endpoints with no fields, call them but treat as info dump
  const fmt = vals.format || action.fields.find(f => f.name === 'format')?.value || 'png';
  const form = new FormData();
  const blob = dataURLtoBlob(img.dataUrl);
  form.append('file', blob, img.name);
  Object.entries(vals).forEach(([k, v]) => { form.append(k, v); });
  // Always include quality/format default
  if (action.key === 'convert' || action.key === 'upscale-hq' || action.key === 'ultra-clear' || action.key === 'resize') {
    if (!vals.quality) form.append('quality', appSettings.quality / 100);
  }
  const url = '/api/' + action.key;
  const res = await fetch(url, { method: 'POST', body: form });
  if (!res.ok) {
    const t = await res.text();
    throw new Error(t || ('HTTP ' + res.status));
  }
  const ct = res.headers.get('content-type') || '';
  if (ct.startsWith('image/')) {
    const blob = await res.blob();
    const dataUrl = await blobToDataUrl(blob);
    const dims = await getBlobDims(blob);
    return {
      sourceId: img.id,
      name: makeResultName(img.name, fmt),
      dataUrl,
      size: blob.size,
      dim: dims.w + ' × ' + dims.h,
      action: action.key
    };
  } else if (ct.startsWith('application/json')) {
    const j = await res.json();
    // Save as JSON text
    const text = JSON.stringify(j, null, 2);
    const blob = new Blob([text], { type: 'application/json' });
    const dataUrl = await blobToDataUrl(blob);
    return {
      sourceId: img.id,
      name: makeResultName(img.name, 'json'),
      dataUrl,
      size: blob.size,
      dim: 'JSON',
      action: action.key
    };
  } else {
    // Text or other
    const t = await res.text();
    const blob = new Blob([t], { type: 'text/plain;charset=utf-8' });
    const dataUrl = await blobToDataUrl(blob);
    return {
      sourceId: img.id,
      name: makeResultName(img.name, 'txt'),
      dataUrl,
      size: blob.size,
      dim: 'TEXT',
      action: action.key
    };
  }
}
function blobToDataUrl(blob) {
  return new Promise((resolve, reject) => {
    const r = new FileReader();
    r.onload = () => resolve(r.result);
    r.onerror = () => reject(new Error('blob read error'));
    r.readAsDataURL(blob);
  });
}
function getBlobDims(blob) {
  return new Promise((resolve) => {
    const url = URL.createObjectURL(blob);
    const img = new Image();
    img.onload = () => { resolve({ w: img.naturalWidth, h: img.naturalHeight }); URL.revokeObjectURL(url); };
    img.onerror = () => { resolve({ w: 0, h: 0 }); URL.revokeObjectURL(url); };
    img.src = url;
  });
}

/* ========== Command Palette ========== */
function openCmd() {
  cmdBg.classList.add('show');
  cmdInput.value = '';
  cmdHighlight = 0;
  renderCmdList('');
  setTimeout(() => cmdInput.focus(), 50);
}
function closeCmd() { cmdBg.classList.remove('show'); }
function renderCmdList(q) {
  const ql = q.toLowerCase();
  const items = [];
  ACTIONS.filter(a => !ql || a.label.toLowerCase().includes(ql) || a.desc.toLowerCase().includes(ql))
    .slice(0, 50).forEach(a => {
      items.push({ type: 'action', action: a, name: a.label, sub: a.desc, cat: CATEGORY_LABELS[a.group] || a.group });
    });
  if (!ql || '上传'.includes(ql) || 'upload'.includes(ql)) items.push({ type: 'cmd', name: '上传图片', sub: '选择文件', cat: '文件', act: () => fileInput.click() });
  if (!ql || '清空'.includes(ql) || 'clear'.includes(ql)) items.push({ type: 'cmd', name: '清空全部', sub: '删除所有图片和结果', cat: '文件', act: clearAll });
  if (!ql || '设置'.includes(ql) || 'settings'.includes(ql)) items.push({ type: 'cmd', name: '打开设置', sub: '通用/输出/AI/关于', cat: '应用', act: openSettings });
  if (!ql || '主题'.includes(ql) || 'theme'.includes(ql)) items.push({ type: 'cmd', name: '切换深色模式', sub: '在浅色/深色之间切换', cat: '外观', act: toggleTheme });
  if (!ql || '帮助'.includes(ql) || 'help'.includes(ql)) items.push({ type: 'cmd', name: '快捷键帮助', sub: '查看所有快捷键', cat: '帮助', act: () => helpBg.classList.add('show') });
  cmdList.innerHTML = '';
  items.forEach((it, idx) => {
    const div = document.createElement('div');
    div.className = 'cmd-item' + (idx === cmdHighlight ? ' on' : '');
    const icon = it.type === 'action' ? (ICON[it.action.icon] || ICON.info) : '<svg class="icon" viewBox="0 0 24 24"><polyline points="9 18 15 12 9 6"/></svg>';
    div.innerHTML =
      '<div class="cmd-item-ico">' + icon + '</div>' +
      '<div class="cmd-item-name">' + it.name + '</div>' +
      '<div class="cmd-item-cat">' + (it.cat || '') + '</div>';
    div.addEventListener('click', () => {
      if (it.type === 'action') { closeCmd(); openModal(it.action); }
      else { closeCmd(); it.act && it.act(); }
    });
    div.addEventListener('mouseenter', () => { cmdHighlight = idx; renderCmdList(cmdInput.value); });
    cmdList.appendChild(div);
  });
  if (!items.length) cmdList.innerHTML = '<div style="padding:32px;text-align:center;color:var(--text-3);font-size:13px">没有匹配的命令</div>';
}
function moveCmdHighlight(dir) {
  const items = cmdList.querySelectorAll('.cmd-item');
  if (!items.length) return;
  cmdHighlight = (cmdHighlight + dir + items.length) % items.length;
  items.forEach((it, i) => it.classList.toggle('on', i === cmdHighlight));
  items[cmdHighlight].scrollIntoView({ block: 'nearest' });
}

/* ========== Settings Drawer ========== */
function openSettings() { drawer.classList.add('show'); drawerBg.classList.add('show'); }
function closeSettings() { drawer.classList.remove('show'); drawerBg.classList.remove('show'); }
function renderSwatches() {
  if (!swatches) return;
  swatches.innerHTML = '';
  ACCENTS.forEach(a => {
    const s = document.createElement('div');
    s.className = 'swatch' + (a.id === appSettings.accent ? ' on' : '');
    s.style.background = a.color;
    s.title = a.id;
    s.addEventListener('click', () => {
      appSettings.accent = a.id;
      saveSettings();
      applyAccent();
      renderSwatches();
    });
    swatches.appendChild(s);
  });
}

/* ========== Theme ========== */
function applyTheme() {
  document.documentElement.setAttribute('data-theme', appSettings.dark ? 'dark' : 'light');
  applyAccent();
  if (darkToggle) darkToggle.classList.toggle('on', appSettings.dark);
}
function toggleTheme() {
  appSettings.dark = !appSettings.dark;
  saveSettings();
  applyTheme();
  showToast('已切换到' + (appSettings.dark ? '深色' : '浅色') + '模式', 'success');
}

/* ========== AI Status ========== */
async function checkAiStatus() {
  try {
    const res = await fetch('/api/ai-status');
    if (!res.ok) throw new Error('HTTP ' + res.status);
    const j = await res.json();
    if (aiStatus) aiStatus.textContent = j.message || (j.available ? 'AI 引擎就绪' : 'AI 引擎不可用');
    if (aiPath) aiPath.textContent = j.modelPath || '—';
    if (aiTag) {
      aiTag.textContent = j.available ? '已就绪' : '未安装';
      aiTag.style.color = j.available ? 'var(--success)' : 'var(--text-3)';
    }
  } catch(e) {
    if (aiStatus) aiStatus.textContent = '无法检测 (后端可能未启动)';
    if (aiTag) aiTag.textContent = '—';
  }
}

/* ========== Init ========== */
function init() {
  buildLCats();
  buildPCats();
  renderTools();
  renderPToolList();
  renderStrips();
  renderInfo();
  renderResults();
  renderSwatches();
  applyTheme();
  if (setFmt) setFmt.value = appSettings.fmt;
  if (setQuality) setQuality.value = appSettings.quality;
  if (setQualityVal) setQualityVal.textContent = appSettings.quality;
  if (setNaming) setNaming.value = appSettings.naming;
  if (autoDlToggle) autoDlToggle.classList.toggle('on', appSettings.autoDl);
  if (keepOrigToggle) keepOrigToggle.classList.toggle('on', appSettings.keepOrig);
  if (compactToggle) compactToggle.classList.toggle('on', appSettings.compact);
  if (infoToggle) infoToggle.classList.toggle('on', appSettings.showInfo);
  if (setConcurrency) setConcurrency.value = String(appSettings.concurrency);
  if (lStatTools) lStatTools.textContent = ACTIONS.length;
  if (aboutCount) aboutCount.textContent = ACTIONS.length + ' 项';

  // Bind events
  bindUpload(btnUploadL);
  bindUpload(btnUploadP);
  if (pBtnPick) pBtnPick.addEventListener('click', () => fileInput.click());
  if (btnThemeL) btnThemeL.addEventListener('click', toggleTheme);
  if (btnThemeP) btnThemeP.addEventListener('click', toggleTheme);
  if (btnSettingsL) btnSettingsL.addEventListener('click', openSettings);
  if (btnSettingsP) btnSettingsP.addEventListener('click', openSettings);
  if (drawerClose) drawerClose.addEventListener('click', closeSettings);
  if (drawerBg) drawerBg.addEventListener('click', closeSettings);
  if (lBtnClear) lBtnClear.addEventListener('click', clearAll);
  if (lBtnHelp) lBtnHelp.addEventListener('click', () => helpBg.classList.add('show'));
  if (lBtnDlAll) lBtnDlAll.addEventListener('click', downloadAll);
  if (pBtnDlAll) pBtnDlAll.addEventListener('click', downloadAll);
  if (modalClose) modalClose.addEventListener('click', closeModal);
  if (modalCancel) modalCancel.addEventListener('click', closeModal);
  if (modalSubmit) modalSubmit.addEventListener('click', submitModal);
  if (helpClose) helpClose.addEventListener('click', () => helpBg.classList.remove('show'));
  if (helpOk) helpOk.addEventListener('click', () => helpBg.classList.remove('show'));

  // Tabs (landscape right)
  document.querySelectorAll('.l-tab').forEach(t => {
    t.addEventListener('click', () => {
      document.querySelectorAll('.l-tab').forEach(x => x.classList.remove('on'));
      document.querySelectorAll('.l-pane').forEach(x => x.classList.remove('on'));
      t.classList.add('on');
      document.querySelector('.l-pane[data-lpane=' + t.dataset.ltab + ']').classList.add('on');
    });
  });
  // Tabs (drawer)
  document.querySelectorAll('.drawer-tab').forEach(t => {
    t.addEventListener('click', () => {
      document.querySelectorAll('.drawer-tab').forEach(x => x.classList.remove('on'));
      document.querySelectorAll('.drawer-pane').forEach(x => x.classList.remove('on'));
      t.classList.add('on');
      document.querySelector('.drawer-pane[data-dpane=' + t.dataset.dtab + ']').classList.add('on');
    });
  });
  // Modal scope pills
  if (modalScopeEl) {
    modalScopeEl.querySelectorAll('.scope-pill').forEach(p => {
      p.addEventListener('click', () => {
        modalScope = p.dataset.scope;
        modalScopeEl.querySelectorAll('.scope-pill').forEach(x => x.classList.toggle('on', x === p));
      });
    });
  }
  // File input
  if (fileInput) fileInput.addEventListener('change', (e) => { addFiles(e.target.files); fileInput.value = ''; });
  // Drag & drop (both layouts)
  ['lCanvas'].forEach(id => {
    const c = $(id);
    if (!c) return;
    c.addEventListener('dragover', (e) => { e.preventDefault(); c.classList.add('drag'); });
    c.addEventListener('dragleave', () => c.classList.remove('drag'));
    c.addEventListener('drop', (e) => {
      e.preventDefault();
      c.classList.remove('drag');
      if (e.dataTransfer.files.length) addFiles(e.dataTransfer.files);
    });
  });
  // Whole window drop
  window.addEventListener('dragover', (e) => e.preventDefault());
  window.addEventListener('drop', (e) => {
    e.preventDefault();
    if (e.dataTransfer.files.length) addFiles(e.dataTransfer.files);
  });

  // Search
  if (searchL) searchL.addEventListener('input', () => renderTools());

  // Command palette
  if (cmdInput) {
    cmdInput.addEventListener('input', () => { cmdHighlight = 0; renderCmdList(cmdInput.value); });
    cmdInput.addEventListener('keydown', (e) => {
      if (e.key === 'ArrowDown') { e.preventDefault(); moveCmdHighlight(1); }
      else if (e.key === 'ArrowUp') { e.preventDefault(); moveCmdHighlight(-1); }
      else if (e.key === 'Enter') {
        e.preventDefault();
        const it = cmdList.querySelectorAll('.cmd-item')[cmdHighlight];
        if (it) it.click();
      } else if (e.key === 'Escape') { closeCmd(); }
    });
  }
  if (cmdBg) cmdBg.addEventListener('click', (e) => { if (e.target === cmdBg) closeCmd(); });

  // Settings inputs
  if (darkToggle) darkToggle.addEventListener('click', () => { appSettings.dark = !appSettings.dark; saveSettings(); applyTheme(); });
  if (compactToggle) compactToggle.addEventListener('click', () => { appSettings.compact = !appSettings.compact; saveSettings(); compactToggle.classList.toggle('on', appSettings.compact); });
  if (infoToggle) infoToggle.addEventListener('click', () => { appSettings.showInfo = !appSettings.showInfo; saveSettings(); infoToggle.classList.toggle('on', appSettings.showInfo); });
  if (autoDlToggle) autoDlToggle.addEventListener('click', () => { appSettings.autoDl = !appSettings.autoDl; saveSettings(); autoDlToggle.classList.toggle('on', appSettings.autoDl); });
  if (keepOrigToggle) keepOrigToggle.addEventListener('click', () => { appSettings.keepOrig = !appSettings.keepOrig; saveSettings(); keepOrigToggle.classList.toggle('on', appSettings.keepOrig); });
  if (aiFallback) aiFallback.addEventListener('click', () => aiFallback.classList.toggle('on'));
  if (setFmt) setFmt.addEventListener('change', () => { appSettings.fmt = setFmt.value; saveSettings(); });
  if (setQuality) setQuality.addEventListener('input', () => { appSettings.quality = parseInt(setQuality.value); setQualityVal.textContent = setQuality.value; saveSettings(); });
  if (setNaming) setNaming.addEventListener('change', () => { appSettings.naming = setNaming.value; saveSettings(); });
  if (setConcurrency) setConcurrency.addEventListener('change', () => { appSettings.concurrency = parseInt(setConcurrency.value); saveSettings(); });

  // Keyboard shortcuts
  document.addEventListener('keydown', (e) => {
    const mod = e.metaKey || e.ctrlKey;
    if (mod && e.key === 'k') { e.preventDefault(); openCmd(); }
    else if (mod && e.key === 'o') { e.preventDefault(); fileInput.click(); }
    else if (mod && e.key === 'd') { e.preventDefault(); toggleTheme(); }
    else if (mod && e.key === ',') { e.preventDefault(); openSettings(); }
    else if (mod && e.shiftKey && (e.key === 'Backspace' || e.key === 'Delete')) { e.preventDefault(); clearAll(); }
    else if (mod && e.key === 's') { e.preventDefault(); downloadAll(); }
    else if (e.key === 'Escape') {
      if (cmdBg.classList.contains('show')) closeCmd();
      else if (drawer.classList.contains('show')) closeSettings();
      else if (modalBg.classList.contains('show')) closeModal();
      else if (helpBg.classList.contains('show')) helpBg.classList.remove('show');
    }
  });

  checkAiStatus();
  logMsg('Studio 已就绪 · ' + ACTIONS.length + ' 项工具', 'ok');
}

init();
