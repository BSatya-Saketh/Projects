# Personal Portfolio Website

## Overview
This is a modern, professional portfolio website for Satya Saketh Bollepally. It's built with HTML, CSS, and JavaScript (no frameworks) to ensure easy deployment and maintenance.

## Features
- Responsive design that works on all devices
- Modern and clean UI with smooth animations
- Sections for showcasing skills, certifications, experience, projects, and contact information
- Profile picture display with professional styling
- Resume download option
- Social media links
- GitHub projects integration

## File Structure
- `index.html` - Main HTML file
- `styles.css` - CSS styling
- `script.js` - JavaScript functionality
- `Profile.jpeg` - Profile picture
- `Satya Saketh Bollepally.pdf` - Resume file

## Customization Guide

### Personal Information
Update the following sections in `index.html` to personalize the portfolio:

1. **Header/Navigation**: Update the logo text if needed
2. **Hero Section**: Update your name, professional title, and brief description
3. **About Section**: Add your personal background, education details, and stats
4. **Skills Section**: Modify the skills to match your expertise
5. **Certifications Section**: Add your certifications with issuing organizations
6. **Experience Section**: Update with your work history
7. **Projects Section**: Add your own projects with descriptions and links
8. **Contact Section**: Update your email, phone, location, and social media links

### Styling
The website uses a color scheme defined in the `:root` section of `styles.css`. You can modify these variables to change the color scheme:

```css
:root {
    --primary-color: #2563eb;
    --secondary-color: #1e40af;
    --accent-color: #3b82f6;
    /* other color variables */
}
```

### Certifications Section
The portfolio includes a dedicated certifications section to showcase your credentials. To customize:

1. Locate the certifications section in `index.html`:
```html
<section id="certifications" class="certifications">
    <div class="container">
        <h2 class="section-title">Certifications</h2>
        <div class="certifications-container">
            <!-- Certification cards go here -->
        </div>
    </div>
</section>
```

2. Add or modify certification cards following this structure:
```html
<div class="certification-card">
    <div class="certification-icon">
        <i class="fas fa-certificate"></i> <!-- Choose appropriate Font Awesome icon -->
    </div>
    <h3 class="certification-name">Certification Title</h3>
    <p class="certification-issuer">Issuing Organization</p>
    <div class="certification-details">
        <p>Brief description of the certification</p>
    </div>
</div>
```

3. Update the stats in the About section to reflect the correct number of certifications.

### Profile Picture
The portfolio includes a profile picture in the hero section. To update your profile picture:

1. Replace the existing image file `Profile.jpeg` with your own image (keeping the same filename)

2. Or modify the image tag in the hero section of `index.html`:
```html
<div class="hero-image">
    <div class="profile-image">
        <img src="your-new-image.jpg" alt="Your Name" class="profile-pic">
    </div>
</div>
```

3. The CSS styling is already set up in `styles.css` with appropriate sizing, border-radius, and shadow effects.

## Deployment Instructions

### GitHub Pages Deployment

1. Create a new GitHub repository
2. Push all the files to the repository:
```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/yourusername/your-repo-name.git
git push -u origin main
```

3. Go to the repository settings on GitHub
4. Navigate to the "Pages" section
5. Under "Source", select "main" branch
6. Click "Save"
7. Your site will be published at `https://yourusername.github.io/your-repo-name/`

### Alternative Deployment Options

#### Netlify
1. Sign up for a Netlify account
2. Click "New site from Git"
3. Connect to your GitHub repository
4. Deploy the site

#### Vercel
1. Sign up for a Vercel account
2. Import your GitHub repository
3. Deploy the site

## Browser Compatibility
This website is compatible with all modern browsers including:
- Google Chrome
- Mozilla Firefox
- Safari
- Microsoft Edge

## Performance Optimization
The website is optimized for performance with:
- Minified CSS and JavaScript (can be further optimized)
- Optimized images (add your own optimized images)
- Efficient animations that don't impact performance

## Future Enhancements
- Blog section
- Portfolio filtering options
- Testimonials section
- Language switcher
- Interactive certification badges
- Project image gallery

---

Feel free to customize this portfolio to showcase your unique skills and experiences!