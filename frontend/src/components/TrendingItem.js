import React from 'react';
import {
  ListItem,
  ListItemText,
  Typography,
  Chip,
  Box,
  Link,
} from '@mui/material';
import { format } from 'date-fns';
import { zhCN } from 'date-fns/locale';

function TrendingItem({ item, showDate = false, showPlatform = false }) {
  const handleClick = () => {
    if (item.url) {
      window.open(item.url, '_blank');
    }
  };

  // Format number to display with comma separators and add "+" for large numbers
  const formatNumber = (num) => {
    if (num === null || num === undefined) return '';
    return num > 10000
      ? Math.floor(num / 10000) + '万+'
      : num.toLocaleString();
  };

  return (
    <ListItem 
      className="trending-item"
      alignItems="flex-start" 
      sx={{ 
        cursor: item.url ? 'pointer' : 'default',
        borderBottom: '1px solid #eee',
        '&:hover': {
          backgroundColor: 'rgba(0, 0, 0, 0.04)',
        },
      }}
      onClick={handleClick}
    >
      <Box 
        sx={{ 
          display: 'flex', 
          alignItems: 'center', 
          mr: 2, 
          minWidth: '30px'
        }}
      >
        <Typography 
          variant="body1" 
          sx={{ 
            fontWeight: 'bold',
            color: item.rank <= 3 ? 'error.main' : 'text.secondary',
          }}
        >
          {item.rank}
        </Typography>
      </Box>
      
      <ListItemText
        primary={
          <Box sx={{ display: 'flex', alignItems: 'center', flexWrap: 'wrap' }}>
            <Typography 
              component="span"
              variant="body1"
              sx={{ fontWeight: 'medium', mr: 1 }}
            >
              {item.title}
            </Typography>
            
            {showPlatform && (
              <Chip 
                size="small" 
                label={item.platform.displayName} 
                sx={{ mr: 1, mb: 0.5 }}
              />
            )}
            
            {item.hotValue && (
              <Chip 
                size="small"
                label={formatNumber(item.hotValue)}
                color="secondary"
                variant="outlined"
                sx={{ mb: 0.5 }}
              />
            )}
          </Box>
        }
        secondary={
          <React.Fragment>
            {item.description && (
              <Typography
                component="span"
                variant="body2"
                color="text.secondary"
                sx={{ display: 'block', mt: 0.5 }}
              >
                {item.description}
              </Typography>
            )}
            
            {showDate && item.fetchTime && (
              <Typography
                component="span"
                variant="caption"
                color="text.secondary"
                sx={{ display: 'block', mt: 0.5 }}
              >
                {format(new Date(item.fetchTime), 'yyyy-MM-dd HH:mm', { locale: zhCN })}
              </Typography>
            )}
          </React.Fragment>
        }
      />
    </ListItem>
  );
}

export default TrendingItem; 