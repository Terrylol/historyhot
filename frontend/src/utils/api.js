import axios from 'axios';
import { 
  mockPlatforms, 
  mockLatestTrending,
  mockDates,
  getMockTrendingByPlatform,
  getMockTrendingByDate,
  getMockTrendingByPlatformAndDate
} from './mockData';

const API_URL = '/api';
// 控制是否优先使用模拟数据（开发时可设为true）
const USE_MOCK_DATA = false;

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export const getPlatforms = async () => {
  if (USE_MOCK_DATA) {
    return mockPlatforms;
  }
  
  try {
    const response = await api.get('/platforms');
    return response.data;
  } catch (error) {
    console.error('Error fetching platforms:', error);
    return mockPlatforms;
  }
};

export const getActivePlatforms = async () => {
  if (USE_MOCK_DATA) {
    return mockPlatforms.filter(p => p.active);
  }
  
  try {
    const response = await api.get('/platforms/active');
    return response.data;
  } catch (error) {
    console.error('Error fetching active platforms:', error);
    return mockPlatforms.filter(p => p.active);
  }
};

export const getLatestTrending = async () => {
  if (USE_MOCK_DATA) {
    return mockLatestTrending;
  }
  
  try {
    const response = await api.get('/trending/latest');
    return response.data;
  } catch (error) {
    console.error('Error fetching latest trending searches:', error);
    return mockLatestTrending;
  }
};

export const getTrendingByPlatform = async (platformId) => {
  if (USE_MOCK_DATA) {
    return getMockTrendingByPlatform(platformId);
  }
  
  try {
    const response = await api.get(`/trending/platform/${platformId}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching trending searches for platform ${platformId}:`, error);
    return getMockTrendingByPlatform(platformId);
  }
};

export const getTrendingByPlatformAndDate = async (platformId, date) => {
  if (USE_MOCK_DATA) {
    return getMockTrendingByPlatformAndDate(platformId, date);
  }
  
  try {
    const response = await api.get(`/trending/platform/${platformId}/date/${date}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching trending searches for platform ${platformId} on ${date}:`, error);
    return getMockTrendingByPlatformAndDate(platformId, date);
  }
};

export const getTrendingByDate = async (date) => {
  if (USE_MOCK_DATA) {
    return getMockTrendingByDate(date);
  }
  
  try {
    const response = await api.get(`/trending/date/${date}`);
    return response.data;
  } catch (error) {
    console.error(`Error fetching trending searches for date ${date}:`, error);
    return getMockTrendingByDate(date);
  }
};

export const getAvailableDates = async () => {
  if (USE_MOCK_DATA) {
    return mockDates;
  }
  
  try {
    const response = await api.get('/trending/dates');
    return response.data;
  } catch (error) {
    console.error('Error fetching available dates:', error);
    return mockDates;
  }
};

export const triggerFetch = async () => {
  if (USE_MOCK_DATA) {
    return "Mock trending search fetch triggered successfully";
  }
  
  try {
    const response = await api.post('/trending/fetch');
    return response.data;
  } catch (error) {
    console.error('Error triggering trending search fetch:', error);
    throw error;
  }
}; 