import React from 'react';
import { Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import { zhCN } from '@mui/material/locale';
import Layout from './components/Layout';
import Home from './pages/Home';
import HistoryPage from './pages/HistoryPage';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import zhLocale from 'date-fns/locale/zh-CN';
import MockDataIndicator from './components/MockDataIndicator';

// 是否使用模拟数据的全局配置，与api.js中的USE_MOCK_DATA保持一致
const USE_MOCK_DATA = false;

// 创建主题
const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
    },
    secondary: {
      main: '#dc004e',
    },
    background: {
      default: '#f5f5f5',
    },
  },
  typography: {
    fontFamily: [
      '-apple-system',
      'BlinkMacSystemFont',
      '"Segoe UI"',
      'Roboto',
      '"Helvetica Neue"',
      'Arial',
      'sans-serif',
      '"Apple Color Emoji"',
      '"Segoe UI Emoji"',
      '"Segoe UI Symbol"',
    ].join(','),
  },
}, zhCN);

function App() {
  return (
    <ThemeProvider theme={theme}>
      <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={zhLocale}>
        <CssBaseline />
        <Layout>
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/history" element={<HistoryPage />} />
          </Routes>
          <MockDataIndicator useMockData={USE_MOCK_DATA} position="bottom-right" />
        </Layout>
      </LocalizationProvider>
    </ThemeProvider>
  );
}

export default App; 