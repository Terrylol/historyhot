import React from 'react';
import { Box, Chip, Tooltip } from '@mui/material';
import WarningIcon from '@mui/icons-material/Warning';

/**
 * 模拟数据指示器 - 当使用模拟数据时显示提示信息
 * 
 * @param {Object} props - 组件参数
 * @param {boolean} props.useMockData - 是否正在使用模拟数据
 * @param {string} [props.position='top-right'] - 指示器位置
 * @returns {JSX.Element}
 */
const MockDataIndicator = ({ useMockData = true, position = 'top-right' }) => {
  if (!useMockData) return null;
  
  // 根据位置设置不同的样式
  const positionStyles = {
    'top-right': {
      position: 'fixed',
      top: '70px',
      right: '20px',
      zIndex: 1100
    },
    'top-left': {
      position: 'fixed',
      top: '70px',
      left: '20px',
      zIndex: 1100
    },
    'bottom-right': {
      position: 'fixed',
      bottom: '20px',
      right: '20px',
      zIndex: 1100
    },
    'bottom-left': {
      position: 'fixed',
      bottom: '20px',
      left: '20px',
      zIndex: 1100
    },
    'inline': {
      margin: '10px 0'
    }
  };
  
  return (
    <Box sx={positionStyles[position] || positionStyles['top-right']}>
      <Tooltip title="当前显示的是模拟数据，非真实热搜数据" arrow>
        <Chip
          icon={<WarningIcon />}
          label="模拟数据模式"
          color="warning"
          variant="outlined"
          size="small"
          sx={{ fontWeight: 'medium' }}
        />
      </Tooltip>
    </Box>
  );
};

export default MockDataIndicator; 