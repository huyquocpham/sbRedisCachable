import React from 'react';
import ProductList from './components/ProductList';
import { logger } from './utils/logger';

function App() {
    logger.debug('App component rendering');
    
    return (
        <div style={{ fontFamily: 'Arial, sans-serif', backgroundColor: '#fafafa', minHeight: '100vh' }}>
            <ProductList />
        </div>
    );
}

export default App;

