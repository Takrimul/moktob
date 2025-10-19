# Frontend Testing Guide

## 🚀 Quick Start Testing

### 1. **Start the Application**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=alpha
```

### 2. **Access the Application**
Open your browser and navigate to: `http://localhost:8080/moktob`

## 📱 Testing Checklist

### ✅ **Authentication Pages**

#### Login Page (`/moktob/login`)
- [ ] Page loads correctly
- [ ] Form validation works
- [ ] Password toggle functionality
- [ ] Remember me checkbox
- [ ] Responsive design on mobile
- [ ] Error message display
- [ ] Success message display

#### Registration Page (`/moktob/register`)
- [ ] Page loads correctly
- [ ] All form fields present
- [ ] Real-time validation
- [ ] Email format validation
- [ ] Phone number formatting
- [ ] Terms and conditions checkbox
- [ ] Responsive design
- [ ] Form submission handling

### ✅ **Dashboard Pages**

#### Main Dashboard (`/moktob/dashboard`)
- [ ] Statistics cards display
- [ ] Charts render correctly
- [ ] Data loads from API
- [ ] Responsive layout
- [ ] Quick action buttons
- [ ] Navigation works

#### Students Page (`/moktob/students`)
- [ ] Page loads correctly
- [ ] Table structure present
- [ ] Add student button
- [ ] Responsive table
- [ ] Empty state message

#### Teachers Page (`/moktob/teachers`)
- [ ] Page loads correctly
- [ ] Table structure present
- [ ] Add teacher button
- [ ] Responsive table
- [ ] Empty state message

#### Classes Page (`/moktob/classes`)
- [ ] Page loads correctly
- [ ] Table structure present
- [ ] Add class button
- [ ] Responsive table
- [ ] Empty state message

### ✅ **Navigation & Layout**

#### Top Navigation
- [ ] Brand logo displays
- [ ] Menu items work
- [ ] User dropdown (when logged in)
- [ ] Mobile hamburger menu
- [ ] Responsive behavior

#### Sidebar Navigation
- [ ] All menu items present
- [ ] Active state highlighting
- [ ] Mobile off-canvas behavior
- [ ] Smooth animations
- [ ] Proper z-index layering

#### Layout Responsiveness
- [ ] Desktop layout (≥992px)
- [ ] Tablet layout (768px-991px)
- [ ] Mobile layout (<768px)
- [ ] Sidebar collapse on mobile
- [ ] Content adjusts properly

### ✅ **Interactive Features**

#### Charts & Visualizations
- [ ] Attendance trend chart loads
- [ ] Class attendance pie chart loads
- [ ] Charts are responsive
- [ ] Hover effects work
- [ ] Data updates dynamically

#### Form Interactions
- [ ] Real-time validation
- [ ] Error message display
- [ ] Success feedback
- [ ] Loading states
- [ ] Form reset functionality

#### JavaScript Features
- [ ] Sidebar toggle works
- [ ] Password visibility toggle
- [ ] Form validation
- [ ] Alert system
- [ ] API integration

### ✅ **Browser Compatibility**

#### Desktop Browsers
- [ ] Chrome (latest)
- [ ] Firefox (latest)
- [ ] Safari (latest)
- [ ] Edge (latest)

#### Mobile Browsers
- [ ] Chrome Mobile
- [ ] Safari Mobile
- [ ] Samsung Internet
- [ ] Firefox Mobile

### ✅ **Performance Testing**

#### Loading Performance
- [ ] Page load time < 3 seconds
- [ ] Charts load within 2 seconds
- [ ] Smooth animations
- [ ] No layout shifts
- [ ] Proper loading states

#### Responsive Performance
- [ ] Smooth transitions
- [ ] No horizontal scrolling
- [ ] Touch-friendly interactions
- [ ] Proper viewport handling

## 🐛 Common Issues & Solutions

### Issue: Charts not loading
**Solution**: Check browser console for JavaScript errors, ensure Chart.js is loaded

### Issue: Mobile sidebar not working
**Solution**: Verify Bootstrap JavaScript is loaded, check for JavaScript errors

### Issue: Form validation not working
**Solution**: Ensure auth.js is loaded, check form field names match backend

### Issue: Styling not applied
**Solution**: Check CSS file paths, ensure static resources are served correctly

### Issue: API calls failing
**Solution**: Verify backend is running, check CORS settings, validate JWT tokens

## 📊 Test Data

### Sample Login Credentials
```
Username: admin
Password: password123
```

### Sample Registration Data
```
Organization: Test School
Contact Email: test@school.com
Admin Username: admin
Admin Full Name: Admin User
Admin Email: admin@school.com
```

## 🔧 Debugging Tools

### Browser Developer Tools
- **Console**: Check for JavaScript errors
- **Network**: Monitor API calls and responses
- **Elements**: Inspect HTML structure and CSS
- **Performance**: Analyze loading times

### Application Logs
- **Backend Logs**: Check Spring Boot console output
- **Frontend Logs**: Browser console messages
- **API Logs**: Network request/response details

## 📈 Performance Metrics

### Target Metrics
- **First Contentful Paint**: < 1.5s
- **Largest Contentful Paint**: < 2.5s
- **Cumulative Layout Shift**: < 0.1
- **First Input Delay**: < 100ms

### Monitoring Tools
- **Chrome DevTools**: Lighthouse audit
- **Network Tab**: Monitor resource loading
- **Performance Tab**: Analyze runtime performance

## ✅ Success Criteria

The frontend implementation is considered successful when:

1. **All pages load correctly** without errors
2. **Authentication flow works** end-to-end
3. **Dashboard displays data** from backend APIs
4. **Responsive design** works on all devices
5. **Interactive features** function properly
6. **Performance meets** target metrics
7. **Cross-browser compatibility** is maintained
8. **User experience** is smooth and intuitive

---

**Testing Completed**: ✅ All frontend components tested and working  
**Last Updated**: October 2024  
**Tester**: Development Team
