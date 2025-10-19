# Email Configuration Setup

## Gmail App Password Setup

To send emails from `takrimul25@gmail.com`, you need to set up an App Password:

1. **Enable 2-Factor Authentication** on your Gmail account
2. **Generate App Password**:
   - Go to Google Account settings
   - Security → 2-Step Verification → App passwords
   - Generate a new app password for "Mail"
   - Copy the 16-character password

3. **Set Environment Variable**:
   ```bash
   # Windows
   set MAIL_PASSWORD=your-16-character-app-password
   
   # Linux/Mac
   export MAIL_PASSWORD=your-16-character-app-password
   ```

## Testing Email Functionality

After setting up the app password, test the client registration:

```bash
curl -X POST http://localhost:8080/moktob/api/clients/register \
  -H "Content-Type: application/json" \
  -d '{
    "clientName": "Test School",
    "contactEmail": "test@example.com",
    "contactPhone": "+1234567890",
    "address": "Test Address",
    "subscriptionPlan": "BASIC",
    "expiryDate": "2024-12-31",
    "adminUsername": "test_admin",
    "adminFullName": "Test Admin",
    "adminEmail": "admin@example.com",
    "adminPhone": "+1234567890"
  }'
```

The system will:
1. Create the client and admin user
2. Send an email with login credentials to the client's contact email
3. Return the registration response

## Email Template

The email will contain:
- Welcome message
- Organization name
- Admin username and temporary password
- Login instructions
- Contact information

## Troubleshooting

If emails fail to send:
1. Check the app password is correct
2. Verify 2FA is enabled on Gmail
3. Check application logs for detailed error messages
4. Ensure the MAIL_PASSWORD environment variable is set
