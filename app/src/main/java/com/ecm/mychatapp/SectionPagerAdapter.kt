package com.ecm.mychatapp



import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class SectionPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> UsersFragment()
            1 -> ChatsFragment()
            else -> throw IllegalArgumentException("Invalid position: $position")
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0 -> return "USERS"
            1 -> return  "CHATS"
        }
        return null!!
    }
}


//class SectionPagerAdapter (fm : FragmentManager) : FragmentPagerAdapter(fm) {
//    //    override fun getCount(position: Int): Fragment {
////       when(position){
////           0 -> {
////               return UsersFragment
////           }
////           1 -> {
////               return ChatsFragment
////           }
////       }
////        return null!!
////    }
////
////    override fun getItem(position: Int): Fragment {
////        return 2
////    }
//    override fun getCount(position: Int): Int {
//        when(position){
//                0 -> return UsersFragment
//                1 -> return ChatsFragment
//        }
//        return null!!
//    }
//
//    override fun getItem(position: Int): Fragment {
//        return 2
//    }
//
//}

