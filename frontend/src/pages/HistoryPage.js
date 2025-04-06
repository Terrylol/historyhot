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
  MenuItem,
  FormControl,
  InputLabel,
  Select,
} from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { format, parseISO } from 'date-fns';
import TrendingItem from '../components/TrendingItem';
import { 
  getActivePlatforms, 
  getAvailableDates, 
  getTrendingByDate,
  getTrendingByPlatformAndDate 
} from '../utils/api';

function HistoryPage() {
  const [platforms, setPlatforms] = useState([]);
  const [availableDates, setAvailableDates] = useState([]);
  const [trendingData, setTrendingData] = useState([]);
  const [selectedDate, setSelectedDate] = useState(null);
  const [selectedPlatformIndex, setSelectedPlatformIndex] = useState(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // 初始化加载平台列表和可用日期
  useEffect(() => {
    const fetchInitialData = async () => {
      setLoading(true);
      setError(null);
      try {
        // 获取平台列表
        const platformsData = await getActivePlatforms();
        setPlatforms(platformsData);
        
        // 获取可用日期列表
        const dates = await getAvailableDates();
        setAvailableDates(dates);
        
        // 默认选择最新日期
        if (dates.length > 0) {
          setSelectedDate(dates[0]);
        }
      } catch (err) {
        setError('获取初始数据失败，请稍后重试');
        console.error('Error fetching initial data:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchInitialData();
  }, []);

  // 当选择的日期变化时，加载该日期的热搜数据
  useEffect(() => {
    const fetchTrendingData = async () => {
      if (!selectedDate) return;
      
      setLoading(true);
      setError(null);
      try {
        let data;
        if (selectedPlatformIndex === 0) {
          // 全部平台
          data = await getTrendingByDate(selectedDate);
        } else {
          // 特定平台
          const platformId = platforms[selectedPlatformIndex - 1]?.id;
          if (platformId) {
            data = await getTrendingByPlatformAndDate(platformId, selectedDate);
          }
        }
        setTrendingData(data || []);
      } catch (err) {
        setError('获取热搜数据失败，请稍后重试');
        console.error('Error fetching trending data:', err);
      } finally {
        setLoading(false);
      }
    };

    fetchTrendingData();
  }, [selectedDate, selectedPlatformIndex, platforms]);

  // 处理平台标签切换
  const handlePlatformChange = (event, newValue) => {
    setSelectedPlatformIndex(newValue);
  };

  // 处理日期选择
  const handleDateChange = (event) => {
    setSelectedDate(event.target.value);
  };

  // 全平台标签加上所有平台的数据
  const tabsWithAll = [{ id: 0, displayName: '全部' }, ...platforms];

  return (
    <Box sx={{ width: '100%' }}>
      <Typography variant="h5" gutterBottom>
        历史热搜数据
      </Typography>

      <Box sx={{ mb: 3, display: 'flex', flexDirection: { xs: 'column', sm: 'row' }, gap: 2 }}>
        <FormControl fullWidth sx={{ maxWidth: { sm: 300 } }}>
          <InputLabel id="date-select-label">选择日期</InputLabel>
          <Select
            labelId="date-select-label"
            id="date-select"
            value={selectedDate || ''}
            label="选择日期"
            onChange={handleDateChange}
            disabled={availableDates.length === 0 || loading}
          >
            {availableDates.map((date) => (
              <MenuItem key={date} value={date}>
                {date}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      </Box>

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
              {tabsWithAll.map((platform) => (
                <Tab key={platform.id} label={platform.displayName} />
              ))}
            </Tabs>
          </Paper>

          {trendingData.length === 0 ? (
            <Alert severity="info" sx={{ my: 2 }}>
              {selectedDate ? `${selectedDate} 没有找到热搜数据` : '请选择日期查看历史热搜数据'}
            </Alert>
          ) : (
            <Paper>
              <List sx={{ bgcolor: 'background.paper' }}>
                {trendingData.map(item => (
                  <TrendingItem 
                    key={item.id} 
                    item={item} 
                    showPlatform={selectedPlatformIndex === 0} 
                    showDate={true}
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

export default HistoryPage; 