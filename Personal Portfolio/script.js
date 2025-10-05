document.addEventListener('DOMContentLoaded', function() {
    // Variables
    const header = document.querySelector('header');
    const hamburger = document.querySelector('.hamburger');
    const navLinks = document.querySelector('.nav-links');
    const navLinksItems = document.querySelectorAll('.nav-links a');
    const sections = document.querySelectorAll('section');
    const contactForm = document.querySelector('.contact-form');

    // Sticky Header
    window.addEventListener('scroll', function() {
        if (window.scrollY > 50) {
            header.classList.add('scrolled');
        } else {
            header.classList.remove('scrolled');
        }
        
        // Active nav link based on scroll position
        let current = '';
        
        sections.forEach(section => {
            const sectionTop = section.offsetTop - 100;
            const sectionHeight = section.clientHeight;
            
            if (window.scrollY >= sectionTop && window.scrollY < sectionTop + sectionHeight) {
                current = section.getAttribute('id');
            }
        });
        
        navLinksItems.forEach(link => {
            link.classList.remove('active');
            if (link.getAttribute('href').substring(1) === current) {
                link.classList.add('active');
            }
        });
    });

    // Mobile Menu Toggle
    hamburger.addEventListener('click', function() {
        hamburger.classList.toggle('active');
        navLinks.classList.toggle('active');
    });

    // Close mobile menu when clicking on a nav link
    navLinksItems.forEach(item => {
        item.addEventListener('click', function() {
            hamburger.classList.remove('active');
            navLinks.classList.remove('active');
        });
    });

    // Smooth scrolling for navigation links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function(e) {
            e.preventDefault();
            
            const targetId = this.getAttribute('href');
            const targetElement = document.querySelector(targetId);
            
            if (targetElement) {
                window.scrollTo({
                    top: targetElement.offsetTop - 80,
                    behavior: 'smooth'
                });
            }
        });
    });

    // Form submission
    if (contactForm) {
        contactForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // Get form data
            const formData = new FormData(this);
            const formEntries = Object.fromEntries(formData.entries());
            
            // Here you would typically send the form data to a server
            // For now, we'll just show a success message
            
            // Clear form fields
            this.reset();
            
            // Show success message
            const successMessage = document.createElement('div');
            successMessage.classList.add('success-message');
            successMessage.textContent = 'Your message has been sent successfully!';
            successMessage.style.color = 'var(--success-color)';
            successMessage.style.marginTop = '15px';
            successMessage.style.fontWeight = '500';
            
            this.appendChild(successMessage);
            
            // Remove success message after 5 seconds
            setTimeout(() => {
                successMessage.remove();
            }, 5000);
        });
    }

    // Animation on scroll
    const animateOnScroll = function() {
        const elements = document.querySelectorAll('.animate-on-scroll');
        
        elements.forEach(element => {
            const elementPosition = element.getBoundingClientRect().top;
            const windowHeight = window.innerHeight;
            
            if (elementPosition < windowHeight - 100) {
                element.classList.add('animated');
            }
        });
    };
    
    // Add animation classes to elements
    const addAnimationClasses = function() {
        // Add animation classes to section titles
        document.querySelectorAll('.section-title').forEach(title => {
            title.classList.add('animate-on-scroll');
        });
        
        // Add animation classes to skill cards
        document.querySelectorAll('.skill-card').forEach(card => {
            card.classList.add('animate-on-scroll');
        });
        
        // Add animation classes to timeline items
        document.querySelectorAll('.timeline-item').forEach(item => {
            item.classList.add('animate-on-scroll');
        });
        
        // Add animation classes to project cards
        document.querySelectorAll('.project-card').forEach(card => {
            card.classList.add('animate-on-scroll');
        });
    };
    
    // Initialize animations
    addAnimationClasses();
    window.addEventListener('scroll', animateOnScroll);
    animateOnScroll(); // Run once on page load

    // Typing effect for hero section
    const typingEffect = function() {
        const element = document.querySelector('.hero-content h2');
        if (!element) return;
        
        const text = element.textContent;
        element.textContent = '';
        
        let i = 0;
        const typing = setInterval(() => {
            if (i < text.length) {
                element.textContent += text.charAt(i);
                i++;
            } else {
                clearInterval(typing);
            }
        }, 100);
    };
    
    // Initialize typing effect
    setTimeout(typingEffect, 1000);

    // Skills progress animation
    const animateSkillProgress = function() {
        const skillBars = document.querySelectorAll('.skill-progress');
        
        skillBars.forEach(bar => {
            const percentage = bar.getAttribute('data-percentage');
            bar.style.width = '0%';
            
            setTimeout(() => {
                bar.style.width = percentage + '%';
            }, 500);
        });
    };
    
    // Initialize skill progress animation
    setTimeout(animateSkillProgress, 1000);
});