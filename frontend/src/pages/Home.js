import React, { useState, useEffect } from 'react';
import {
  Box,
  Typography,
  List,
  Tab,
  Tabs,
  Paper,
  CircularProgress,
  Alert,
} from '@mui/material';
import TrendingItem from '../components/TrendingItem';
import { getActivePlatforms, getLatestTrending } from '../utils/api';

function Home() {
  const [platforms, setPlatforms] = useState([]);
  const [trendingData, setTrendingData] = useState([]);
  const [selectedPlatformIndex, setSelectedPlatformIndex] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      setError(null);
      try {
        // 获取平台列表
        const platformsData = await getActivePlatforms();
        setPlatforms(platformsData);
        
        // 获取最新热搜数据
        const trendingResult = await getLatestTrending();
        setTrendingData(trendingResult);
      } catch (err) {
        setError('获取数据失败，请稍后重试');
        console.error('Error fetching data:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  // 处理平台标签切换
  const handlePlatformChange = (event, newValue) => {
    setSelectedPlatformIndex(newValue);
  };

  // 根据选择的平台过滤数据
  const filteredTrending = selectedPlatformIndex === 0
    ? trendingData
    : trendingData.filter(item => 
        item.platform.id === platforms[selectedPlatformIndex - 1]?.id
      );

  // 全平台标签加上所有平台的数据
  const tabsWithAll = [{ id: 0, displayName: '全部' }, ...platforms];

  return (
    <Box sx={{ width: '100%' }}>
      <Typography variant="h5" gutterBottom>
        最新热搜数据
      </Typography>

      {loading ? (
        <Box sx={{ display: 'flex', justifyContent: 'center', my: 4 }}>
          <CircularProgress />
        </Box>
      ) : error ? (
        <Alert severity="error" sx={{ my: 2 }}>{error}</Alert>
      ) : (
        <>
          <Paper sx={{ mb: 2 }}>
            <Tabs
              value={selectedPlatformIndex}
              onChange={handlePlatformChange}
              variant="scrollable"
              scrollButtons="auto"
            >
              {tabsWithAll.map((platform, index) => (
                <Tab key={platform.id} label={platform.displayName} />
              ))}
            </Tabs>
          </Paper>

          {filteredTrending.length === 0 ? (
            <Alert severity="info" sx={{ my: 2 }}>
              暂无热搜数据，请点击右上角的"手动获取"按钮获取最新数据
            </Alert>
          ) : (
            <Paper>
              <List sx={{ bgcolor: 'background.paper' }}>
                {filteredTrending.map(item => (
                  <TrendingItem 
                    key={item.id} 
                    item={item} 
                    showPlatform={selectedPlatformIndex === 0} 
                  />
                ))}
              </List>
            </Paper>
          )}
        </>
      )}
    </Box>
  );
}

export default Home; 