# UI Design Principles – Healthcare Appointment & EMR System

## 1. Consistency
**Why:** Consistent use of fonts, colors, and layout makes the interface predictable and user-friendly.  
**Application:** Every page uses the same header, footer, font styles, and button colors.

## 2. Visual Hierarchy
**Why:** Helps users focus on the most important information first.  
**Application:**
- Larger font and bold weight for headings (e.g., “Book Appointment”)
- Primary buttons in a prominent color
- Subtle shades for secondary actions

## 3. Typography
- **Font Family Used:** `Inter` or `Roboto` (depending on client support)
- **Reason:** These are modern, legible, sans-serif fonts ideal for medical UIs.
- **Font Sizes:**
    - Heading: 24–32px
    - Subheading: 18–20px
    - Body: 14–16px

**Why:** Improves readability across screens.

## 4. Color Scheme

| Element         | Color       | Reason                          |
|----------------|-------------|----------------------------------|
| Primary Color  | #2B7A78     | Calm teal blue, instills trust   |
| Accent Color   | #3AAFA9     | For CTAs (Call-to-Actions)       |
| Background     | #F5F5F5     | Soft light grey for less strain  |
| Text Color     | #17252A     | Dark grey for strong readability |
| Error Color    | #FF6B6B     | Recognizable red for errors      |
| Success Color  | #28A745     | Universal green for success      |

**Why:** Colors chosen to convey trust, calmness, and clarity in a healthcare environment.

## 5. Alignment & Spacing
**Why:** Aligned content improves scannability. Spacing gives breathing room.  
**Application:**
- Left-aligned text for form fields
- 16px padding around major components
- Grid layout using 12-column design in desktop view

## 6. Accessibility (A11y)
**Why:** To ensure the interface is usable by everyone, including users with disabilities.  
**Application:**
- Sufficient color contrast (WCAG AA compliant)
- Large, tappable buttons
- Labels on all inputs

## 7. Minimalism and Clarity
**Why:** Avoid overwhelming the user. Focus on functionality.  
**Application:**
- Clear labels (e.g., “Login”, “Book Slot”)
- No unnecessary animations
- Simple iconography used only where helpful

## 8. Feedback & Affordance
**Why:** The user must know when an action succeeds or fails.  
**Application:**
- Green checkmark or success toast after form submit
- Red error text under invalid fields
- Hover and focus states for interactive elements

## 9. Mobile Responsiveness
**Why:** Many users will access via mobile.  
**Application:**
- Responsive design with media queries
- Mobile-first layout with collapsible menus

## 10. Branding
**Why:** Ensures professionalism and user trust.  
**Application:**
- Custom logo and favicon
- Consistent use of color palette across assets
- About page clearly states mission and services
