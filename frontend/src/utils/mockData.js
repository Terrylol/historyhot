// 模拟平台数据
export const mockPlatforms = [
  {
    id: 1,
    name: "weibo",
    displayName: "微博热搜",
    apiUrl: "https://weibo.com/ajax/side/hotSearch",
    description: "新浪微博热搜榜单",
    active: true
  },
  {
    id: 2,
    name: "baidu",
    displayName: "百度热搜",
    apiUrl: "https://top.baidu.com/api/board?platform=wise&tab=realtime",
    description: "百度搜索风云榜",
    active: true
  },
  {
    id: 3,
    name: "zhihu",
    displayName: "知乎热榜",
    apiUrl: "https://www.zhihu.com/api/v3/feed/topstory/hot-lists/total?limit=50",
    description: "知乎热门话题",
    active: true
  }
];

// 各平台热搜标题样本
const weiboTopics = [
  "国庆长假返程高峰开启",
  "中国女排获得巴黎奥运会参赛资格",
  "专家解读新冠疫情最新形势",
  "多地房价下调引发热议",
  "新能源汽车销量再创新高",
  "AI绘画引发版权争议",
  "某知名演员宣布结婚",
  "高校新生报到现场爆满",
  "世界杯预选赛国足首战告捷",
  "台风\"海葵\"即将登陆",
  "大学生就业形势分析",
  "中秋月饼价格调查",
  "秋季过敏高发期如何防治",
  "手机厂商发布新款折叠屏",
  "跨境电商新政策实施",
  "网红餐厅卫生问题曝光",
  "5G应用加速普及",
  "老年人被骗案例激增",
  "演唱会门票黄牛乱象",
  "青年创业新趋势报告"
];

const baiduTopics = [
  "世界经济论坛最新报告",
  "北方多地迎来降温",
  "科学家发现新型抗生素",
  "中小学推行新教材",
  "高铁票价调整公告",
  "网络安全宣传周活动开启",
  "研究显示：每天步行5000步就有益健康",
  "央行降息对房贷影响分析",
  "多地取消公积金提取限制",
  "上市公司财报季观察",
  "电影票价格为何持续上涨",
  "智能家居普及率调查",
  "常见食品添加剂安全解读",
  "年轻人不爱存钱现象调查",
  "汽车自动驾驶技术最新进展",
  "大学专业就业前景排行",
  "全国医保统筹政策落地",
  "新能源发电占比创新高",
  "直播带货新规实施后效果",
  "外卖平台佣金调整引发争议"
];

const zhihuTopics = [
  "为什么越来越多年轻人选择\"工作摸鱼\"？",
  "如何看待\"超前消费\"现象？",
  "人工智能会替代哪些工作岗位？",
  "每天只睡6小时，长期来看会对健康造成哪些影响？",
  "为什么身边的年轻人都开始\"养生\"了？",
  "读博士到底值不值得？",
  "如何评价最新发布的iPhone 15系列？",
  "平价与奢侈品牌的差距到底在哪里？",
  "三十岁还没结婚的人都在想什么？",
  "现在的大学生为什么都不谈恋爱了？",
  "如何看待\"35岁职场危机\"？",
  "副业收入超过主业是一种什么体验？",
  "为什么大城市的年轻人都不想回老家发展？",
  "电动汽车真的比燃油车省钱吗？",
  "如何看待互联网大厂裁员潮？",
  "为什么越来越多的人不爱社交了？",
  "现在的年轻人为什么不爱存钱？",
  "如何评价最近大火的《编译型语言》电影？",
  "为什么越来越多人选择独居？",
  "长期熬夜的人后来都怎么样了？"
];

// 生成模拟的热搜数据
const generateMockTrending = (platform, date, count = 20) => {
  const fetchTime = new Date(date);
  // 添加随机分钟数，使每次生成的时间有所不同
  fetchTime.setMinutes(Math.floor(Math.random() * 60));
  fetchTime.setSeconds(Math.floor(Math.random() * 60));
  const items = [];
  let topicsArray;
  
  // 根据平台选择相应的话题列表
  if (platform.name === "weibo") {
    topicsArray = weiboTopics;
  } else if (platform.name === "baidu") {
    topicsArray = baiduTopics;
  } else if (platform.name === "zhihu") {
    topicsArray = zhihuTopics;
  } else {
    topicsArray = weiboTopics; // 默认使用微博话题
  }
  
  // 对话题进行随机排序
  const shuffledTopics = [...topicsArray].sort(() => 0.5 - Math.random());
  
  // 随机选择要展示的话题数量（但不少于10个）
  const randomCount = Math.max(10, Math.floor(Math.random() * count));
  
  for (let i = 1; i <= Math.min(randomCount, shuffledTopics.length); i++) {
    const title = shuffledTopics[i-1];
    
    // 随机热度值，使热度排名更真实
    const hotValue = Math.floor(Math.random() * 10000000) + 100000 - (i * 50000);
    
    // 为不同平台添加不同的描述
    let description = null;
    if (platform.name === "zhihu" && i % 2 === 0) {
      description = `关于${title}的讨论，吸引了众多网友参与。有人认为这是社会变革的体现，也有人表示这反映了当代年轻人的生活态度变化。`;
    } else if (platform.name === "baidu" && i % 3 === 0) {
      description = `最新数据显示，关于${title}的搜索量在过去24小时内增长了300%。`;
    } else if (platform.name === "weibo" && i % 4 === 0) {
      description = `#${title}# 相关话题已有超过${Math.floor(Math.random() * 100) + 10}万讨论，${Math.floor(Math.random() * 10) + 3}亿阅读。`;
    }
    
    items.push({
      id: `${platform.id}-${date.replace(/-/g, '')}-${i}`,
      platform: platform,
      title: title,
      url: "#",
      rank: i,
      hotValue: hotValue,
      description: description,
      fetchTime: fetchTime.toISOString(),
      fetchDate: date
    });
  }
  
  return items;
};

// 生成最近7天的日期
const generateDates = (count = 7) => {
  const dates = [];
  const today = new Date();
  
  for (let i = 0; i < count; i++) {
    const date = new Date(today);
    date.setDate(date.getDate() - i);
    dates.push(date.toISOString().split('T')[0]);
  }
  
  return dates;
};

// 模拟可用日期列表
export const mockDates = generateDates();

// 生成所有平台最近7天的热搜数据
export const mockTrendingData = (() => {
  let allData = [];
  
  mockDates.forEach(date => {
    mockPlatforms.forEach(platform => {
      const platformData = generateMockTrending(platform, date);
      allData = [...allData, ...platformData];
    });
  });
  
  return allData;
})();

// 获取最新日期的热搜数据
export const mockLatestTrending = mockTrendingData.filter(item => item.fetchDate === mockDates[0]);

// 按平台筛选数据
export const getMockTrendingByPlatform = (platformId) => {
  return mockTrendingData.filter(item => item.platform.id === platformId);
};

// 按日期筛选数据
export const getMockTrendingByDate = (date) => {
  return mockTrendingData.filter(item => item.fetchDate === date);
};

// 按平台和日期筛选数据
export const getMockTrendingByPlatformAndDate = (platformId, date) => {
  return mockTrendingData.filter(
    item => item.platform.id === platformId && item.fetchDate === date
  );
}; 