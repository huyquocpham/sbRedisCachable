import axios from 'axios';
import { BehaviorSubject, from, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { logger } from '../utils/logger';

const API_URL = '/api/products';

logger.info('ProductService initializing with API_URL:', API_URL);

const productsSubject = new BehaviorSubject([]);
const loadingSubject = new BehaviorSubject(false);
const errorSubject = new BehaviorSubject(null);

export const products$ = productsSubject.asObservable();
export const loading$ = loadingSubject.asObservable();
export const error$ = errorSubject.asObservable();

const fetchProducts = () => {
    logger.info('Fetching all products from API');
    loadingSubject.next(true);
    errorSubject.next(null);
    
    return from(axios.get(API_URL)).pipe(
        map(response => response.data),
        tap(products => {
            logger.info(`Fetched ${products.length} products`);
            productsSubject.next(products);
            loadingSubject.next(false);
        }),
        catchError(error => {
            logger.error('Failed to fetch products:', error.message);
            loadingSubject.next(false);
            errorSubject.next(error.message || 'Failed to fetch products');
            return throwError(() => error);
        })
    );
};

const createProduct = (product) => {
    logger.info('Creating product:', product);
    loadingSubject.next(true);
    errorSubject.next(null);
    
    return from(axios.post(API_URL, product)).pipe(
        map(response => response.data),
        tap((createdProduct) => {
            logger.info('Product created successfully:', createdProduct);
            fetchProducts().subscribe();
        }),
        catchError(error => {
            logger.error('Failed to create product:', error.message);
            loadingSubject.next(false);
            errorSubject.next(error.message || 'Failed to create product');
            return throwError(() => error);
        })
    );
};

const updateProduct = (id, product) => {
    logger.info(`Updating product ${id}:`, product);
    loadingSubject.next(true);
    errorSubject.next(null);
    
    return from(axios.put(`${API_URL}/${id}`, product)).pipe(
        map(response => response.data),
        tap((updatedProduct) => {
            logger.info(`Product ${id} updated successfully:`, updatedProduct);
            fetchProducts().subscribe();
        }),
        catchError(error => {
            logger.error(`Failed to update product ${id}:`, error.message);
            loadingSubject.next(false);
            errorSubject.next(error.message || 'Failed to update product');
            return throwError(() => error);
        })
    );
};

const deleteProduct = (id) => {
    logger.info(`Deleting product ${id}`);
    loadingSubject.next(true);
    errorSubject.next(null);
    
    return from(axios.delete(`${API_URL}/${id}`)).pipe(
        tap(() => {
            logger.info(`Product ${id} deleted successfully`);
            fetchProducts().subscribe();
        }),
        catchError(error => {
            logger.error(`Failed to delete product ${id}:`, error.message);
            loadingSubject.next(false);
            errorSubject.next(error.message || 'Failed to delete product');
            return throwError(() => error);
        })
    );
};

export const productService = {
    fetchProducts,
    createProduct,
    updateProduct,
    deleteProduct
};

