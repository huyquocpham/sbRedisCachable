import React, { useState, useEffect } from 'react';
import { productService, products$, loading$, error$ } from '../services/productService';
import { Subscription } from 'rxjs';
import { logger } from '../utils/logger';

const ProductList = () => {
    logger.info('ProductList component initializing');
    
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [newProduct, setNewProduct] = useState({
        name: '',
        description: '',
        price: '',
        stockQuantity: '',
        category: ''
    });
    const [editingProduct, setEditingProduct] = useState(null);

    useEffect(() => {
        logger.info('ProductList useEffect - subscribing to observables');
        const subscription = new Subscription();
        
        subscription.add(
            products$.subscribe(data => {
                logger.debug(`Products updated: ${data.length} items`);
                setProducts(data);
            })
        );
        subscription.add(
            loading$.subscribe(isLoading => {
                logger.debug(`Loading state changed: ${isLoading}`);
                setLoading(isLoading);
            })
        );
        subscription.add(
            error$.subscribe(err => {
                if (err) {
                    logger.warn('Error state updated:', err);
                }
                setError(err);
            })
        );

        logger.info('ProductList - fetching initial products');
        productService.fetchProducts().subscribe();

        return () => {
            logger.info('ProductList component unmounting - unsubscribing');
            subscription.unsubscribe();
        };
    }, []);

    const handleInputChange = (e, isEditing = false) => {
        const { name, value } = e.target;
        logger.debug(`Input changed: ${name} = ${value}`);
        if (isEditing) {
            setEditingProduct({ ...editingProduct, [name]: value });
        } else {
            setNewProduct({ ...newProduct, [name]: value });
        }
    };

    const handleCreate = (e) => {
        e.preventDefault();
        logger.info('Form submitted - creating product');
        const productToCreate = {
            ...newProduct,
            price: parseFloat(newProduct.price),
            stockQuantity: parseInt(newProduct.stockQuantity)
        };
        productService.createProduct(productToCreate).subscribe({
            next: () => {
                logger.info('Product created - resetting form');
                setNewProduct({ name: '', description: '', price: '', stockQuantity: '', category: '' });
            },
            error: (err) => logger.error('Create product failed:', err)
        });
    };

    const handleUpdate = (e) => {
        e.preventDefault();
        logger.info(`Form submitted - updating product ${editingProduct.id}`);
        const productToUpdate = {
            ...editingProduct,
            price: parseFloat(editingProduct.price),
            stockQuantity: parseInt(editingProduct.stockQuantity)
        };
        productService.updateProduct(editingProduct.id, productToUpdate).subscribe({
            next: () => {
                logger.info('Product updated - exiting edit mode');
                setEditingProduct(null);
            },
            error: (err) => logger.error('Update product failed:', err)
        });
    };

    const handleDelete = (id) => {
        logger.warn(`User initiated delete for product ${id}`);
        if (window.confirm('Are you sure you want to delete this product?')) {
            logger.info(`User confirmed delete for product ${id}`);
            productService.deleteProduct(id).subscribe({
                error: (err) => logger.error('Delete product failed:', err)
            });
        } else {
            logger.info(`User cancelled delete for product ${id}`);
        }
    };

    const startEditing = (product) => {
        logger.info(`Starting edit for product ${product.id}`);
        setEditingProduct({ ...product });
    };

    const cancelEditing = () => {
        logger.info('Edit cancelled');
        setEditingProduct(null);
    };

    if (loading && products.length === 0) {
        return <div style={{ padding: '20px', textAlign: 'center' }}>Loading products...</div>;
    }

    if (error && products.length === 0) {
        return <div style={{ padding: '20px', color: 'red', textAlign: 'center' }}>Error: {error}</div>;
    }

    return (
        <div style={{ padding: '20px', maxWidth: '1200px', margin: '0 auto' }}>
            <h1 style={{ textAlign: 'center', marginBottom: '30px' }}>Product Management</h1>
            
            {error && (
                <div style={{ padding: '10px', backgroundColor: '#ffebee', color: '#c62828', marginBottom: '20px', borderRadius: '4px' }}>
                    Error: {error}
                </div>
            )}

            <div style={{ marginBottom: '30px', padding: '20px', backgroundColor: '#f5f5f5', borderRadius: '8px' }}>
                <h2>{editingProduct ? 'Edit Product' : 'Create New Product'}</h2>
                <form onSubmit={editingProduct ? handleUpdate : handleCreate}>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '15px', marginBottom: '15px' }}>
                        <div>
                            <label>Name:</label>
                            <input
                                type="text"
                                name="name"
                                value={editingProduct ? editingProduct.name : newProduct.name}
                                onChange={(e) => handleInputChange(e, !!editingProduct)}
                                required
                                style={{ width: '100%', padding: '8px', marginTop: '5px' }}
                            />
                        </div>
                        <div>
                            <label>Category:</label>
                            <input
                                type="text"
                                name="category"
                                value={editingProduct ? editingProduct.category : newProduct.category}
                                onChange={(e) => handleInputChange(e, !!editingProduct)}
                                style={{ width: '100%', padding: '8px', marginTop: '5px' }}
                            />
                        </div>
                        <div>
                            <label>Price:</label>
                            <input
                                type="number"
                                name="price"
                                step="0.01"
                                value={editingProduct ? editingProduct.price : newProduct.price}
                                onChange={(e) => handleInputChange(e, !!editingProduct)}
                                required
                                style={{ width: '100%', padding: '8px', marginTop: '5px' }}
                            />
                        </div>
                        <div>
                            <label>Stock Quantity:</label>
                            <input
                                type="number"
                                name="stockQuantity"
                                value={editingProduct ? editingProduct.stockQuantity : newProduct.stockQuantity}
                                onChange={(e) => handleInputChange(e, !!editingProduct)}
                                required
                                style={{ width: '100%', padding: '8px', marginTop: '5px' }}
                            />
                        </div>
                    </div>
                    <div style={{ marginBottom: '15px' }}>
                        <label>Description:</label>
                        <textarea
                            name="description"
                            value={editingProduct ? editingProduct.description : newProduct.description}
                            onChange={(e) => handleInputChange(e, !!editingProduct)}
                            rows="3"
                            style={{ width: '100%', padding: '8px', marginTop: '5px' }}
                        />
                    </div>
                    <div style={{ display: 'flex', gap: '10px' }}>
                        <button type="submit" style={{ padding: '10px 20px', backgroundColor: '#2196f3', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                            {editingProduct ? 'Update Product' : 'Create Product'}
                        </button>
                        {editingProduct && (
                            <button type="button" onClick={cancelEditing} style={{ padding: '10px 20px', backgroundColor: '#757575', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                                Cancel
                            </button>
                        )}
                    </div>
                </form>
            </div>

            <h2>Product List</h2>
            {products.length === 0 ? (
                <p>No products found. Create one above!</p>
            ) : (
                <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '15px' }}>
                    <thead>
                        <tr style={{ backgroundColor: '#2196f3', color: 'white' }}>
                            <th style={{ padding: '12px', textAlign: 'left' }}>ID</th>
                            <th style={{ padding: '12px', textAlign: 'left' }}>Name</th>
                            <th style={{ padding: '12px', textAlign: 'left' }}>Category</th>
                            <th style={{ padding: '12px', textAlign: 'left' }}>Price</th>
                            <th style={{ padding: '12px', textAlign: 'left' }}>Stock</th>
                            <th style={{ padding: '12px', textAlign: 'left' }}>Description</th>
                            <th style={{ padding: '12px', textAlign: 'center' }}>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {products.map((product, index) => (
                            <tr key={product.id} style={{ backgroundColor: index % 2 === 0 ? '#ffffff' : '#f5f5f5' }}>
                                <td style={{ padding: '12px' }}>{product.id}</td>
                                <td style={{ padding: '12px' }}>{product.name}</td>
                                <td style={{ padding: '12px' }}>{product.category}</td>
                                <td style={{ padding: '12px' }}>${product.price}</td>
                                <td style={{ padding: '12px' }}>{product.stockQuantity}</td>
                                <td style={{ padding: '12px' }}>{product.description}</td>
                                <td style={{ padding: '12px', textAlign: 'center' }}>
                                    <button onClick={() => startEditing(product)} style={{ marginRight: '8px', padding: '6px 12px', backgroundColor: '#4caf50', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                                        Edit
                                    </button>
                                    <button onClick={() => handleDelete(product.id)} style={{ padding: '6px 12px', backgroundColor: '#f44336', color: 'white', border: 'none', borderRadius: '4px', cursor: 'pointer' }}>
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
            {loading && <div style={{ textAlign: 'center', padding: '20px' }}>Processing...</div>}
        </div>
    );
};

export default ProductList;

